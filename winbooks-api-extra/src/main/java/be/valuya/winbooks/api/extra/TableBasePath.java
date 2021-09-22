package be.valuya.winbooks.api.extra;

import java.nio.file.Path;
import java.util.Objects;

public class TableBasePath {
    private String tableName;
    private Path basePath;

    public TableBasePath(String tableName, Path basePath) {
        this.tableName = tableName;
        this.basePath = basePath;
    }

    public String getTableName() {
        return tableName;
    }

    public Path getBasePath() {
        return basePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableBasePath that = (TableBasePath) o;
        return Objects.equals(tableName, that.tableName) && Objects.equals(basePath, that.basePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, basePath);
    }

    @Override
    public String toString() {
        return "TableBasePath{" +
                "tableName='" + tableName + '\'' +
                ", basePath=" + basePath +
                '}';
    }
}
