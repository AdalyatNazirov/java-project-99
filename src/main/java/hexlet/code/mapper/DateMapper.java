package hexlet.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class DateMapper {
    @Named("instantToLocalDate")
    public LocalDate instantToLocalDate(Instant instant) {
        return instant != null ? instant.atZone(ZoneOffset.UTC).toLocalDate() : null;
    }

    @Named("localDateToInstant")
    public Instant localDateToInstant(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    }
}
