package org.shonminh.helper.sql;


import org.shonminh.helper.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {

    private List<String> getStatements(String str) {
        String[] splits = str.split("[\n\r]+");
        List<String> list = new ArrayList<>();
        boolean lastHasEndWithSeparator = true;
        for (String split : splits) {
            String string = split.trim();
            // 如果是空或者是注释则过滤
            if ("".equals(string) || StringUtil.isSqlComment(string)) {
                continue;
            }
            if (lastHasEndWithSeparator) {
                list.add(string);
            } else {
                String last = list.get(list.size() - 1);
                last = last + " " + string;
                list.set(list.size() - 1, last);
            }
            lastHasEndWithSeparator = split.endsWith(";");
        }
        return list;
    }

    private boolean checkIfCreateStatement(String string) {
        string = string.toLowerCase();
        Pattern pattern = Pattern.compile("create table (.*)\\((.*)\\)(.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    private void parseStatements(List<String> statements) {
        for (String statement : statements) {
            // 如果不是创建语句，则跳过
            if (!checkIfCreateStatement(statement)) {
                continue;
            }
            statement = statement.toLowerCase().replaceAll("[ ]+", " ");
            int left = statement.indexOf("(");
            int right = statement.lastIndexOf(")");
            String[] s = statement.substring(0, left).split(" ");
            String modelName = "";
            if (s.length > 1) {
                modelName = s[s.length - 1].trim();
                if (modelName.contains(".")) {
                    modelName = modelName.split("\\.")[1];
                }
            }
            if ("".equals(modelName)) {
                continue;
            }
            Model model = new Model(modelName);
            String rowColumns = statement.substring(left + 1, right).trim();
            String[] split = rowColumns.split(",");
            for (String str : split) {
                str = str.trim();

                // 如果是一般的索引直接过滤
                if (str.startsWith("key") || str.startsWith("unique")) {
                    continue;
                }
                String[] s1 = str.split(" ");
                if (s1.length <= 1) {
                    continue;
                }
                if (isPrimaryKey(s1)) { // 设置primary key
                    String primaryKey = s1[s1.length - 1].replace("(", "").replace(")", "");
                    model.setPrimaryKey(primaryKey);
                    continue;
                }
                Column column = new Column();
                column.setRaw(str);
                column.setName(s1[0]);
                column.setType(s1[1]);
                int indexOfDefaultValue = findIndexOfDefaultValue(s1);
                if (indexOfDefaultValue != -1) {
                    column.setDefaultValue(s1[indexOfDefaultValue]);
                }
                if (isNotNull(s1)) {
                    column.setNotNull(true);
                } else {
                    column.setNotNull(false);
                }
                if (isUnsigned(s1)) {
                    column.setUnsigned(true);
                } else {
                    column.setUnsigned(false);
                }
                model.appendColumn(column);
            }
            System.out.println(model.generateGoStruct());
        }

    }


    private boolean isPrimaryKey(String[] split) {
        return split[0].equals("primary") && split[1].equals("key");
    }

    private int findIndexOfDefaultValue(String[] split) {
        int index = 0;
        for (String s : split) {
            if ("default".equals(s)) {
                if (index + 1 <= split.length - 1) {
                    return index + 1;
                }
                return -1;
            }
            index++;
        }
        return -1;
    }

    private boolean isNotNull(String[] split) {
        int index = 0;
        for (String s : split) {
            if ("not".equals(s) && index + 1 <= split.length - 1 && "null".equals(split[index + 1])) {
                return true;
            }
            index++;
        }
        return false;
    }

    private boolean isUnsigned(String[] split) {
        for (String s : split) {
            if ("unsigned".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        FileReader reader = new FileReader("/Users/shonminh/ShopeeProject/stock_server/deploy/sql/20190327_add_compare_stock_detail_tab.sql");
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            sb.append(line);
            sb.append("\n");

        }
        String sql = sb.toString();
        SqlParser sqlParser = new SqlParser();
        List<String> statements = sqlParser.getStatements(sql);
//        System.out.println(sqlParser.checkIfCreateLikeStatement(statements.get(0)));
//        System.out.println(sqlParser.checkIfCreateLikeStatement(statements.get(1)));
//        System.out.println(sqlParser.checkIfCreateLikeStatement(statements.get(2)));
//        System.out.println(sqlParser.checkIfCreateStatement(statements.get(0)));
//        System.out.println(sqlParser.checkIfCreateStatement(statements.get(1)));
//        System.out.println(sqlParser.checkIfCreateStatement(statements.get(2)));
        sqlParser.parseStatements(statements);

    }
}

