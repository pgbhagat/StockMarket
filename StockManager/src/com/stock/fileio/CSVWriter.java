package com.stock.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.stock.log.FileLogger;
import com.stock.query.SymbolChecker;

public class CSVWriter {

	private static final String CSV_HEADER = "Stock Symbol,Current Price,Year Target Price,Year High,Year Low";

	private String targetFile;

	private boolean writeHeader = true;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;

	public CSVWriter(String targetFile) throws IOException {
		new File(targetFile).delete();
		fileWriter = new FileWriter(targetFile, true);
		bufferedWriter = new BufferedWriter(fileWriter);
		this.targetFile = targetFile;
	}

	public void close() {
		FileLogger.getInstance().log("Total invalid stock " + SymbolChecker.getInstance().getInvalidSymbols());
		try {
			if (bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
		} catch (IOException e) {
		}
		try {
			if (fileWriter != null) {
				fileWriter.close();
			}
		} catch (IOException e) {
		}
	}

	public void appendStockDetailsAsCSV(List<StockDetailsPojo> stockDetailList) throws IOException {
		if (stockDetailList == null || stockDetailList.isEmpty()) {
			return;
		}
		try {
			if (writeHeader) {
				bufferedWriter.write(CSV_HEADER);
				bufferedWriter.write("\n\r");
				writeHeader = false;
			}
			for (StockDetailsPojo stockDetail : stockDetailList) {
				bufferedWriter.write(stockDetail.toCSVString());
				bufferedWriter.write("\n\r");
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			FileLogger.getInstance()
					.log("Error while writing the file [ " + targetFile + "], error - " + e.getMessage());
			throw e;
		} finally {
		}

	}

}
