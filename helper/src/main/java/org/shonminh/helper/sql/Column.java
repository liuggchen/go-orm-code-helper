package org.shonminh.helper.sql;

import org.shonminh.helper.util.GoTypeUtil;
import org.shonminh.helper.util.StringUtil;

public class Column {

    private String name;
    private String type;
    private String column;
    private String comment;
    private String defaultValue;
    private boolean isUnsigned;
    private boolean isNotNull;
    private String formatStr;
    private String typeFormatStr;
    private boolean isAutoIncrement;

    // go struct name
    private String structName;
    // go struct type
    private String structType;

    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public String getStructType() {
        return structType;
    }

    public void setStructType(String structType) {
        this.structType = structType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getColumn() {
        return column;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUnsigned() {
        return isUnsigned;
    }

    public void setUnsigned(boolean unsigned) {
        isUnsigned = unsigned;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }


    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }


    public String getFormatStr() {
        return formatStr;
    }

    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
    }

    public String generateColumnStruc(boolean isPrimaryKey) {

        this.setStructName(StringUtil.camelString(this.getName()));
        this.setStructType(GoTypeUtil.Translate2GoType(this.getType(), this.isUnsigned()));

        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        sb.append(StringUtil.camelString(this.name));
        sb.append(this.formatStr);
        sb.append(GoTypeUtil.Translate2GoType(this.type, this.isUnsigned));
        sb.append(this.typeFormatStr);
        sb.append("`gorm:");
        sb.append("\"column:").append(this.getColumn().replaceAll("`", "").trim());
        sb.append(";");
        sb.append("type:").append(this.type.toUpperCase());
        sb.append(";");
        if (isPrimaryKey) {
            sb.append("PRIMARY_KEY;");
        }
        if (this.isAutoIncrement()) {
            sb.append("AUTO_INCREMENT;");
        }
        if (this.isNotNull()) {
            sb.append("NOT NULL");
        }
        sb.append("\"`");
        if (this.comment != null && !this.comment.isEmpty()) {
            sb.append(" // ").append(comment);
        }
        return sb.toString();
    }

    public String getTypeFormatStr() {
        return typeFormatStr;
    }

    public void setTypeFormatStr(String typeFormatStr) {
        this.typeFormatStr = typeFormatStr;
    }

    @Override
    public String toString() {
        return "Column{" + "name='" + name + '\'' + ", type='" + type + '\'' + ", defaultValue='" + defaultValue + '\'' + ", isUnsigned=" + isUnsigned + ", isNotNull=" + isNotNull + ", formatStr='" + formatStr + '\'' + ", typeFormatStr='" + typeFormatStr + '\'' + ", isAutoIncrement=" + isAutoIncrement + ", structName='" + structName + '\'' + ", structType='" + structType + '\'' + '}';
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment.strip().replaceFirst("^['\"]", "").replaceAll("['\"]$", "");
        ;
    }
}
