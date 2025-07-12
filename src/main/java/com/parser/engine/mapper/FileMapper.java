package com.parser.engine.mapper;

import com.parser.engine.dto.response.FileDetailsRespDto;
import com.parser.engine.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {Double.class, Integer.class, ZonedDateTime.class, UUID.class})
public interface FileMapper {

	FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

	FileDetailsRespDto toResponseDto(File file);
}
