package com.parser.engine.mapper;

import com.parser.engine.dto.request.FileDetailsRespDto;
import com.parser.engine.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", imports = {Double.class, Integer.class, ZonedDateTime.class})
public interface FileMapper {

	FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

	FileDetailsRespDto toResponseDto(File file);
}
