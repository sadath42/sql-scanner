
 package com.file.util;

import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class GetScriptsInTheFileTest {
	
	@Test
	public void testGetScripts(){
		
		List<String> scripts = GetScriptsInTheFile.getScripts("src/test/resources/box1.txt", new HashSet<>(), null);
		Assert.assertEquals(5, scripts.size());
	}
}
  