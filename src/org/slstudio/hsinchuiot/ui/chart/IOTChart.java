package org.slstudio.hsinchuiot.ui.chart;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.achartengine.chart.TimeChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class IOTChart extends TimeChart {

	private String[] unitLable;

	public IOTChart(XYMultipleSeriesDataset dataset,
			XYMultipleSeriesRenderer renderer) {
		super(dataset, renderer);
	}

	public String[] getUnitLable() {
		return unitLable;
	}

	public void setUnitLable(String[] unitLable) {
		this.unitLable = unitLable;
	}

	/**
	 * The graphical representation of the labels on the Y axis.
	 * 
	 * @param allYLabels
	 *            the Y labels values
	 * @param canvas
	 *            the canvas to paint to
	 * @param paint
	 *            the paint to be used for drawing
	 * @param maxScaleNumber
	 *            the maximum scale number
	 * @param left
	 *            the left value of the labels area
	 * @param right
	 *            the right value of the labels area
	 * @param bottom
	 *            the bottom value of the labels area
	 * @param yPixelsPerUnit
	 *            the amount of pixels per one unit in the chart labels
	 * @param minY
	 *            the minimum value on the Y axis in the chart
	 */
	@Override
	protected void drawYLabels(Map<Integer, List<Double>> allYLabels,
			Canvas canvas, Paint paint, int maxScaleNumber, int left,
			int right, int bottom, double[] yPixelsPerUnit, double[] minY) {
		Orientation or = mRenderer.getOrientation();
		boolean showGridX = mRenderer.isShowGridX();
		boolean showLabels = mRenderer.isShowLabels();
		for (int i = 0; i < maxScaleNumber; i++) {
			paint.setTextAlign(mRenderer.getYLabelsAlign(i));
			List<Double> yLabels = allYLabels.get(i);
			int length = yLabels.size();
			for (int j = 0; j < length; j++) {
				double label = yLabels.get(j);
				Align axisAlign = mRenderer.getYAxisAlign(i);
				boolean textLabel = mRenderer.getYTextLabel(label, i) != null;
				float yLabel = (float) (bottom - yPixelsPerUnit[i]
						* (label - minY[i]));
				if (or == Orientation.HORIZONTAL) {
					if (showLabels && !textLabel) {
						paint.setColor(mRenderer.getYLabelsColor(i));
						if (axisAlign == Align.LEFT) {
							canvas.drawLine(left + getLabelLinePos(axisAlign),
									yLabel, left, yLabel, paint);
							drawText(
									canvas,
									getLabelWithUnit(
											mRenderer.getLabelFormat(), label,
											i),
									left - mRenderer.getYLabelsPadding(),
									yLabel
											- mRenderer
													.getYLabelsVerticalPadding(),
									paint, mRenderer.getYLabelsAngle());
						} else {
							canvas.drawLine(right, yLabel, right
									+ getLabelLinePos(axisAlign), yLabel, paint);
							drawText(
									canvas,
									getLabelWithUnit(
											mRenderer.getLabelFormat(), label,
											i),
									right + mRenderer.getYLabelsPadding(),
									yLabel
											- mRenderer
													.getYLabelsVerticalPadding(),
									paint, mRenderer.getYLabelsAngle());
						}
					}
					if (showGridX) {
						paint.setColor(mRenderer.getGridColor(i));
						canvas.drawLine(left, yLabel, right, yLabel, paint);
					}
				} else if (or == Orientation.VERTICAL) {
					if (showLabels && !textLabel) {
						paint.setColor(mRenderer.getYLabelsColor(i));
						canvas.drawLine(right - getLabelLinePos(axisAlign),
								yLabel, right, yLabel, paint);
						drawText(
								canvas,
								getLabelWithUnit(mRenderer.getLabelFormat(),
										label, i),
								right + 10 + mRenderer.getYLabelsPadding(),
								yLabel - mRenderer.getYLabelsVerticalPadding(),
								paint, mRenderer.getYLabelsAngle());
					}
					if (showGridX) {
						paint.setColor(mRenderer.getGridColor(i));
						canvas.drawLine(right, yLabel, left, yLabel, paint);
					}
				}
			}
		}
	}

	private int getLabelLinePos(Align align) {
		int pos = 4;
		if (align == Align.LEFT) {
			pos = -pos;
		}
		return pos;
	}

	protected String getLabelWithUnit(NumberFormat format, double label,
			int scale) {
		String text = "";
		if (format != null) {
			text = format.format(label);
		} else if (label == Math.round(label)) {
			text = Math.round(label) + "";
		} else {
			text = label + "";
		}
		if (scale > unitLable.length - 1) {
			return text;
		} else {
			return text + unitLable[scale];
		}
	}
}
