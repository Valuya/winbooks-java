package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.ArchivePathNotFoundException;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

class WinbooksPathUtils {
    private static Logger LOGGER = Logger.getLogger(WinbooksPathUtils.class.getName());


    static Optional<Path> getBookYearBasePath(WinbooksFileConfiguration fileConfiguration, WbBookYearFull wbBookYearFull) {
        boolean resolveArchivePaths = fileConfiguration.isResolveArchivePaths();
        boolean ignoreMissingArchives = fileConfiguration.isIgnoreMissingArchives();
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        Path baseFolderPath = fileConfiguration.getBaseFolderPath();

        try {
            if (resolveArchivePaths && isArchivedBookYear(wbBookYearFull)) {
                Path archivePath = resolveArchivePathOrThrow(baseFolderPath, wbBookYearFull, resolveCaseInsensitiveSiblings);
                return Optional.of(archivePath);
            } else {
                return Optional.of(baseFolderPath);
            }
        } catch (ArchivePathNotFoundException archivePathNotFoundException) {
            if (ignoreMissingArchives) {
                return Optional.empty();
            } else {
                throw new WinbooksException(WinbooksError.BOOKYEAR_NOT_FOUND, archivePathNotFoundException);
            }
        }
    }


    static Optional<Path> resolvePath(Path parentPath, String fileName, boolean resolveCaseInsensitiveSiblings) {
        if (parentPath == null) {
            return Optional.empty();
        }
        Path defaultPath = parentPath.resolve(fileName);
        if (Files.exists(defaultPath)) {
            return Optional.of(defaultPath);
        }
        boolean parentExists = Files.exists(parentPath);
        if (!parentExists) {
            return Optional.empty();
        }
        if (fileName.endsWith(".dbf")) {
            String capitalizedExtensionFileName = fileName.replace(".dbf", ".DBF");
            Path capitalizedExtensionPath = parentPath.resolve(capitalizedExtensionFileName);
            if (Files.exists(capitalizedExtensionPath)) {
                return Optional.of(capitalizedExtensionPath);
            }
        }
        if (resolveCaseInsensitiveSiblings) {
            return WinbooksPathUtils.resolveCaseInsensitivePathOptional(parentPath, fileName);
        } else {
            return Optional.empty();
        }
    }

    static Stream<Path> streamDirectoryFiles(Path path, Predicate<Path> acceptFilePredicate) {
        try {
            return Files.find(path, Integer.MAX_VALUE,
                    (visitedPath, attr) -> checkAcceptFilePredicate(acceptFilePredicate, visitedPath, attr));
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }


    static LocalDateTime getLastModifiedTime(Path documentPath) {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(documentPath);
            return toLocalDateTime(lastModifiedTime);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    static LocalDateTime getCreationTime(Path documentPath) {
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(documentPath, BasicFileAttributes.class);
            FileTime creationFileTime = basicFileAttributes.creationTime();
            return toLocalDateTime(creationFileTime);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }


    private static Optional<Path> resolveCaseInsensitivePathOptional(Path parentPath, String fileName) {
        try {
            // Find another child of parent with a similar name
            long time0 = System.currentTimeMillis();
            Optional<Path> firstFoundPathOptional = findSiblingWithSameName(parentPath, fileName);
            long timeAfterWalk = System.currentTimeMillis();
            long deltaTimeWalk = timeAfterWalk - time0;
            LOGGER.log(Level.FINE, "****FIND TIME (" + fileName + "): " + deltaTimeWalk);
            return firstFoundPathOptional;
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }


    private static Optional<Path> findSiblingWithSameName(Path parentPath, String fileName) throws IOException {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> isSamePathNameIgnoreCase(path, fileName);
        return Files.find(parentPath, 1, predicate).findFirst();
    }


    private static boolean isSamePathNameIgnoreCase(Path path, String fileName) {
        return Optional.ofNullable(path.getFileName())
                .map(Path::toString)
                .map(fileName::equalsIgnoreCase)
                .orElse(false);
    }


    private static boolean checkAcceptFilePredicate(Predicate<Path> acceptPredicate, Path visitedPath, BasicFileAttributes attr) {
        boolean directory = attr.isDirectory();
        if (!directory) {
            return acceptPredicate.test(visitedPath);
        } else {
            return false;
        }
    }

    private static LocalDateTime toLocalDateTime(FileTime fileTime) {
        Instant lastModifiedInstant = fileTime.toInstant();
        return LocalDateTime.ofInstant(lastModifiedInstant, ZoneId.systemDefault());
    }

    private static boolean isArchivedBookYear(WbBookYearFull wbBookYearFull) {
        Optional<String> archivePathNameOptional = Optional.ofNullable(wbBookYearFull)
                .flatMap(WbBookYearFull::getArchivePathNameOptional);
        return archivePathNameOptional.isPresent();
    }


    private static Path resolveArchivePathOrThrow(Path baseFolderPath, WbBookYearFull wbBookYearFull, boolean resolveCaseInsitiveSiblings) throws ArchivePathNotFoundException {
        return resolveBookYearArchivePath(baseFolderPath, wbBookYearFull, resolveCaseInsitiveSiblings)
                .orElseThrow(() -> new ArchivePathNotFoundException(baseFolderPath, wbBookYearFull));
    }

    private static Optional<Path> resolveBookYearArchivePath(Path baseFolderPath, WbBookYearFull wbBookYearFull, boolean resolveCaseInsensitiveSibling) {
        if (!isArchivedBookYear(wbBookYearFull)) {
            return Optional.empty();
        }
        String archiveFileName = wbBookYearFull.getArchivePathNameOptional().orElseThrow(IllegalStateException::new);
        return resolveArchivePath(baseFolderPath, archiveFileName, resolveCaseInsensitiveSibling);
    }


    private static Optional<Path> resolveArchivePath(Path baseFolderPath, String archivePathName, boolean resolveCaseInsensitiveSiblings) {
        String archiveFolderName = archivePathName
                .replace("\\", "/")
                .replaceAll("/$", "")
                .replaceAll("^.*/", "");
        Path baseParent = baseFolderPath.getParent();
        return Optional.ofNullable(baseParent)
                .flatMap(parent -> resolvePath(parent, archiveFolderName, resolveCaseInsensitiveSiblings));
    }

}
