package be.valuya.winbooks.api.extra;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 *
 * @author Yannick
 */
public class WinbooksFileConfiguration {

    private String username;
    private String password;
    private Path baseFolderPath;
    private String baseName;
    private Charset charset = StandardCharsets.ISO_8859_1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Path getBaseFolderPath() {
        return baseFolderPath;
    }

    public void setBaseFolderPath(Path baseFolderPath) {
        this.baseFolderPath = baseFolderPath;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
