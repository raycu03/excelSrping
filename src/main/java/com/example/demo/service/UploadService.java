package com.example.demo.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
	

	public Map<String, List<Map<String, String>>> upload(MultipartFile file) throws Exception {
		//asignar una direccion de memoria al archivo excel
		Path tempDir= Files.createTempDirectory("");
		
		//asignar el archivo a una variable
		File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
		file.transferTo(tempFile);
		
		//workbook con tiene todo nuestro archivo excel
		Workbook workbook = WorkbookFactory.create(tempFile);
		//sheet hojas
		Stream<Sheet> sheets = StreamSupport.stream(workbook.spliterator(), false);
		
		//mapa con una lista donde se guardaran todos los valores
		Map<String, List<Map<String, String>>> rowStreampSupplier  = new HashMap<>();
		
		//objener numero de hojas 
		int hojas= workbook.getNumberOfSheets();
		System.out.println("numero de hojas " +hojas);
		
		//recorremos todas del hojas del excel
		sheets.forEach(sheet ->{ 
			
			//obtenemos todas las filas de cada hoja y se crea una lista para guardarlos 
			Stream<Row> rows = StreamSupport.stream(sheet.spliterator(), false);
			List <List<String>> value = new ArrayList<>();
			
			//recorremos las filas
			rows.forEach(row ->{
				//obtenemos todas las celdas y se crea una lista para guardarlos 
				Stream<Cell> cells = StreamSupport.stream(row.spliterator(), false);
				List<String> rowValues = new ArrayList<>();
				
				//recorremos las celdas
				cells.forEach(cell ->{
					//obtenemos todas las celdas y verificamos si esta vacia
					String valueCell = cell.toString();
					if (valueCell  != "") {
						rowValues.add(valueCell);
					}
				});
				if (!rowValues.isEmpty()) {
					
					value.add(rowValues);
				}
			});
			//lista para guardar todas la celdas con datos
			List<Map<String, String>> listRows = new ArrayList<>();
			//obtenemos los nombres de las columnas
			List<String> headersCell = value.get(0);
			// las quitamos para que no salgan coo valores
			value.remove(0);

			//guardamos todo en listRows
			for (List<String> sheetValue : value) {
				Map<String, String> mapCells = IntStream.range(0, headersCell.size())
						.boxed()
						.collect(Collectors
						.toMap(headersCell::get, sheetValue::get));
				listRows.add(mapCells);
			}
			//pasamos todos los datos a rowStreampSupplier que sera lo que se retornara
			rowStreampSupplier.put(sheet.getSheetName(), listRows);
	});
		return rowStreampSupplier;
		}


}
