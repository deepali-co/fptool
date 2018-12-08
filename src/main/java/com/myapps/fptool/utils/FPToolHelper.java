package com.myapps.fptool.utils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

public final class FPToolHelper {

	private FPToolHelper() {
		throw new UnsupportedOperationException("This class cannot be instantiated!");
	}

	public static final List<String> convertToAbsolutePaths(final String basePath, final List<String> fileNames) {
		final String base = addPathSeparatorAtEnd(basePath);
		return fileNames.stream().map(fileName -> base + fileName).collect(Collectors.toCollection(ArrayList::new));
	}

	public static final String convertToAbsolutePath(final String basePath, final String fileName) {
		final String base = addPathSeparatorAtEnd(basePath);
		return base + fileName;
	}

	public static String trimPathSeparatorFromEnd(final String path) {
		if (path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static String addPathSeparatorAtEnd(final String path) {
		return path.endsWith("/") ? path : path + "/";
	}

	public static String[] convertObjectListToStringArray(List<Object> items) {
		if (CollectionUtils.isEmpty(items)) {
			return null;
		}
		return items.stream().map(item -> item.toString()).collect(Collectors.toList()).toArray(new String[0]);
	}

	public static List<String> convertObjectListToListOfStrings(List<Object> items) {
		if (CollectionUtils.isEmpty(items)) {
			return null;
		}
		return items.stream().map(item -> item.toString()).collect(Collectors.toCollection(ArrayList::new));
	}

	public static Date convertStringToDate(String dateStr, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ROOT);
		return sdf.parse(dateStr);
	}

	public static void createReportFile(final String reportFile) throws IOException {
		Path path = Paths.get(reportFile);
		if (path.toFile().exists()) {
			FileChannel.open(Paths.get(reportFile), StandardOpenOption.WRITE).truncate(0).close();
		} else {
			path.toFile().createNewFile();
		}
	}
}
