package com.stock.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stock.cache.StockCacheManager;
import com.stock.fileio.CSVWriter;
import com.stock.fileio.StockDetailsPojo;
import com.stock.log.FileLogger;
import com.stock.main.StockManager;

public class StockCrawlerConsumer implements Runnable {

	CrawledSubResult subResult;
	List<StockDetailsPojo> tmpList = new ArrayList<StockDetailsPojo>();
	CSVWriter writer;

	public StockCrawlerConsumer(CrawledSubResult subResult) {
		this.subResult = subResult;
		try {
			writer = new CSVWriter(StockManager.CSV_FILE);
		} catch (IOException e) {
			FileLogger.getInstance().log("File operation error, will not write csv file, error - " + e.getMessage());
		}
	}

	@Override
	public void run() {

		synchronized (subResult) {
			while (!subResult.isDone) {
				try {
					if (subResult.list.isEmpty()) {
						subResult.wait();
					}
					StockCacheManager.getInstance().putAll(subResult.list);
					if (writer != null) {
						try {
							FileLogger.getInstance()
									.log("Total stock details to be dumped in file " + subResult.list.size());
							writer.appendStockDetailsAsCSV(subResult.list);
						} catch (IOException e) {
							FileLogger.getInstance()
									.log("Warn: Exception in StockCrawlerConsumer.dumpStockDetailsAsCSV, error - "
											+ e.getLocalizedMessage());
						}
					}
				} catch (InterruptedException e) {
					FileLogger.getInstance()
							.log("Warn: Exception in StockCrawlerConsumer, error - " + e.getLocalizedMessage());
				}
				subResult.list.clear();
				subResult.notifyAll();
				System.gc();

			}
			if (!subResult.list.isEmpty()) {
				StockCacheManager.getInstance().putAll(subResult.list);
				if (writer != null) {
					try {
						FileLogger.getInstance()
								.log("Total stock details to be dumped in file " + subResult.list.size());
						writer.appendStockDetailsAsCSV(subResult.list);
					} catch (IOException e) {
						FileLogger.getInstance()
								.log("Warn: Exception in StockCrawlerConsumer.dumpStockDetailsAsCSV, error - "
										+ e.getLocalizedMessage());
					}
				}
				subResult.list.clear();
				subResult.notifyAll();
				System.gc();
			}
		}

	}

}