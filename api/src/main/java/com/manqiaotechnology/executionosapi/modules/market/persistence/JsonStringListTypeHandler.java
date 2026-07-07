package com.manqiaotechnology.executionosapi.modules.market.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonStringListTypeHandler extends BaseTypeHandler<List<String>> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, jsonb(parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static PGobject jsonb(Object value) throws SQLException {
        PGobject object = new PGobject();
        object.setType("jsonb");
        try {
            object.setValue(OBJECT_MAPPER.writeValueAsString(value == null ? List.of() : value));
        } catch (Exception ex) {
            throw new SQLException("Failed to serialize JSONB list", ex);
        }
        return object;
    }

    private static List<String> parse(String value) throws SQLException {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (Exception ex) {
            throw new SQLException("Failed to parse JSONB string list", ex);
        }
    }
}
