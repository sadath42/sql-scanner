package com.file.util;

import org.junit.Assert;
import org.junit.Test;

import com.file.util.vo.BoxChild;

public class SqlExtractorTest {

	@Test
	public void testSqlExtractor() throws Exception {
		BoxChild exctractSqlCommands = SqlExtractor.exctractSqlCommands("src/test/resources/sample.ksh");
		int size = exctractSqlCommands.getSqlComands().size();
		Assert.assertEquals(2, size);
	}

	@Test
	public void testSqlExtractorWithOutSql() throws Exception {
		BoxChild exctractSqlCommands = SqlExtractor.exctractSqlCommands("src/test/resources/box1.txt");
		int size = exctractSqlCommands.getSqlComands().size();
		Assert.assertEquals(0, size);
	}

}
