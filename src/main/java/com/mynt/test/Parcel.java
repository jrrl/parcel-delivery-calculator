package com.mynt.test;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Parcel {
	@Positive
	private final double weight;
	@Positive
	private final double height;
	@Positive
	private final double length;
	@Positive
	private final double width;
	private final BigDecimal volume;

	public Parcel(double weight, double height, double length, double width) {
		this.weight = weight;
		this.height = height;
		this.length = length;
		this.width = width;

		BigDecimal bdHeight = BigDecimal.valueOf(this.height);
		BigDecimal bdWidth = BigDecimal.valueOf(this.width);
		BigDecimal bdLength = BigDecimal.valueOf(this.length);
		volume = bdHeight.multiply(bdWidth).multiply(bdLength);
	}

	public double getWeight() {
		return weight;
	}

	public double getHeight() {
		return height;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}

	public BigDecimal getVolume() {
		return volume;
	}
}
