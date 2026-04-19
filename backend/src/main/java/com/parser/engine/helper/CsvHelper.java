package com.parser.engine.helper;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class CsvHelper {

	/**
	 * Reads a CSV from the given InputStream, maps each row to a DTO, then maps each DTO to an entity.
	 *
	 * @param <D>               DTO class (CSV row mapping)
	 * @param <E>               Entity class (DB entity)
	 * @param inputStream       CSV file input stream
	 * @param dtoClass          DTO class type
	 * @param entityClass       Entity class type (not used directly, but for clarity)
	 * @param dtoToEntityMapper Function to map DTO to Entity
	 * @return List of mapped entities
	 */
	public static <D, E> List<E> readAndMapCsv(InputStream inputStream,
											   Class<D> dtoClass,
											   Class<E> entityClass,
											   Function<D, E> dtoToEntityMapper) {
		List<E> entities = new ArrayList<>();
		try {
			CsvMapper csvMapper = new CsvMapper();
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			MappingIterator<D> it = csvMapper.readerFor(dtoClass).with(schema).readValues(inputStream);
			while (it.hasNext()) {
				D dto = it.next();
				E entity = dtoToEntityMapper.apply(dto);
				entities.add(entity);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error parsing CSV: " + e.getMessage(), e);
		}
		return entities;
	}

	/**
	 * Reads a CSV from the given InputStream, maps each row to a DTO, then maps each DTO to an entity,
	 * and processes entities in batches.
	 *
	 * @param <D>               DTO class (CSV row mapping)
	 * @param <E>               Entity class (DB entity)
	 * @param inputStream       CSV file input stream
	 * @param dtoClass          DTO class type
	 * @param dtoToEntityMapper Function to map DTO to Entity
	 * @param batchSize         Number of entities per batch
	 * @param batchConsumer     Consumer to process each batch of entities
	 */
	public static <D, E> void readAndMapCsvInBatches(InputStream inputStream,
													 Class<D> dtoClass,
													 Function<D, E> dtoToEntityMapper,
													 int batchSize,
													 Consumer<List<E>> batchConsumer) {
		try {
			CsvMapper csvMapper = new CsvMapper();
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			MappingIterator<D> it = csvMapper.readerFor(dtoClass).with(schema).readValues(inputStream);

			List<E> batch = new ArrayList<>(batchSize);
			while (it.hasNext()) {
				D dto = it.next();
				E entity = dtoToEntityMapper.apply(dto);
				batch.add(entity);

				if (batch.size() == batchSize) {
					batchConsumer.accept(new ArrayList<>(batch));
					batch.clear();
				}
			}
			// Process any remaining entities
			if (!batch.isEmpty()) {
				batchConsumer.accept(batch);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error parsing CSV: " + e.getMessage(), e);
		}
	}
}