package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class, SlugMapper.class, DateMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "labels", source = "labelIds")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDate")
    @Mapping(target = "labelIds", source = "labels")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus.slug", source = "status")
    @Mapping(target = "labels", source = "labelIds")
    public abstract void update(TaskUpdateDTO taskStatusUpdateDTO, @MappingTarget Task taskStatus);

    protected abstract Set<Label> mapLabelIds(Set<Long> value);

    protected Set<Long> mapLabels(Set<Label> labels) {
        if (labels == null) {
            return new HashSet<>();
        }
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
