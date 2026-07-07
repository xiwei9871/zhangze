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
public class JsonLongListTypeHandler extends BaseTypeHandler<List<Long>> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<Number>> NUMBER_LIST = new TypeReference<>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        PGobject object = new PGobject();
        object.setType("jsonb");
        try {
            object.setValue(OBJECT_MAPPER.writeValueAsString(parameter == null ? List.of() : parameter));
        } catch (Exception ex) {
            throw new SQLException("Failed to serialize JSONB long list", ex);
        }
        ps.setObject(i, object);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static List<Long> parse(String value) throws SQLException {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(value, NUMBER_LIST).stream().map(Number::longValue).toList();
        } catch (Exception ex) {
            throw new SQLException("Failed to parse JSONB long list", ex);
        }
    }
}
