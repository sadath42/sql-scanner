package com.file.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.file.util.constants.PATTERN;
import com.file.util.vo.BoxChild;
import com.file.util.vo.KshChild;

public class SqlExtractorTest {

	@Test
	public void testSqlExtractor() throws Exception {
		KshChild exctractSqlCommands = SqlExtractor.exctractSqlCommands("src/test/resources/sample.ksh", null);
		int size = exctractSqlCommands.getSqlComands().size();
		Assert.assertEquals(2, size);
	}

	@Test
	public void testSqlExtractorWithOutSql() throws Exception {
		KshChild exctractSqlCommands = SqlExtractor.exctractSqlCommands("src/test/resources/box1.txt", null);
		int size = exctractSqlCommands.getSqlComands().size();
		Assert.assertEquals(0, size);
	}

	@Test
	public void testSQLParam() {

		String command = "${ch_job}_a.dynamic.sql.param";
		Matcher matcher = PATTERN.PARAM_ARG_PATTERN.matcher(command);
		if (matcher.find()) {
			System.out.println("------------>" + matcher.group());
			System.out.println(command.replace(matcher.group(), "sam"));
		}

	}
}
