package com.demo.http.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.util.Scanner;
import com.demo.util.Scanner.ClassEntry;
import com.demo.util.Scanner.ScanMatcher;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class Codes {

	private static final Logger logger = LoggerFactory.getLogger(Codes.class);

	private static List<Class<?>> declaredClasses;

	public static List<Class<?>> getClasses(String includes, String excludes) {
		if (declaredClasses == null) {
			declaredClasses = getClasses0(new ScanMatcher(includes), new ScanMatcher(excludes));
		}
		return declaredClasses;
	}

	private static List<Class<?>> getClasses0(ScanMatcher includes, ScanMatcher excludes) {
		List<ClassEntry> entries = Scanner.scan(includes, excludes);
		List<String> names = new ArrayList<>();
		for (ClassEntry entry : entries) {
			names.add(entry.name);
		}
		return loadClasses(names);
	}

	private static List<Class<?>> loadClasses(List<String> names) {
		List<Class<?>> clazzes = new ArrayList<>();
		for (String name : names) {
			try {
				clazzes.add(Class.forName(name));
			} catch (Throwable e) {// ignore
				logger.debug("load class error", e);
			}
		}
		return clazzes;
	}

}
