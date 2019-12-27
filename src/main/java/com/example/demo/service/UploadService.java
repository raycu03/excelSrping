package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.formula.functions.Roman;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.util.UploadUtil;

@Service
public class UploadService {
	
	private final UploadUtil uploadUtil;
	
	public UploadService(UploadUtil uploadUtil) {
		this.uploadUtil=uploadUtil;
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, String>> upload(MultipartFile file) throws Exception {
		Path tempDir= Files.createTempDirectory("");
		
		File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
		
		file.transferTo(tempFile);
		
		Workbook workbook = WorkbookFactory.create(tempFile);
		
		int hojas= workbook.getNumberOfSheets();
		System.out.println("hojas " +hojas);
		
	
		for (int i = 0; i < hojas; i++) {
			System.out.println(i);
			Sheet sheet = workbook.getSheetAt(1);
			Supplier <Stream<Row>> rowStreampSupplier = uploadUtil.getRowStreamSupplier(sheet);

		Row headerRow =  rowStreampSupplier.get().findFirst().get();
		
		List<String> headerCell = StreamSupport.stream(headerRow.spliterator(), false)
				.map(Cell::toString)
				.collect(Collectors.toList());
		
		//System.out.println(headerCell);

		int col = headerCell.size();
		
		return rowStreampSupplier.get()
			.map(row ->{
			List<String> cellList = StreamSupport.stream(row.spliterator(), false)
					.map(Cell::toString)
					.collect(Collectors.toList());
			
			return IntStream.range(0, col)
					.boxed()
					.collect(Collectors.toMap(
							index ->headerCell.get(index),
							index -> cellList.get(index)));

		}).collect(Collectors.toList());		
	}
		return null;
		}


}
