package com.powers.apmd5.checksum;

import java.io.File;

public interface ChecksumCalculator {

	public String calculate(String str);
	public String calculate(File file);
}
