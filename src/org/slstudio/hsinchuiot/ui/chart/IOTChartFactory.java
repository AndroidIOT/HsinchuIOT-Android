package org.slstudio.hsinchuiot.ui.chart;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;

public class IOTChartFactory {

	public static final GraphicalView getIOTChartView(Context context,
			XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
			String format, String[] unitLable) {
		checkParameters(dataset, renderer);
		IOTChart chart = new IOTChart(dataset, renderer);
		chart.setDateFormat(format);
		chart.setUnitLable(unitLable);
		return new GraphicalView(context, chart);
	}

	private static void checkParameters(XYMultipleSeriesDataset dataset,
			XYMultipleSeriesRenderer renderer) {
		if (dataset == null
				|| renderer == null
				|| dataset.getSeriesCount() != renderer
						.getSeriesRendererCount()) {
			throw new IllegalArgumentException(
					"Dataset and renderer should be not null and should have the same number of series");
		}
	}

}
