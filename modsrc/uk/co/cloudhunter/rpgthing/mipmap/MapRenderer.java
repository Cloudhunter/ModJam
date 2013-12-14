package uk.co.cloudhunter.rpgthing.mipmap;

public class MapRenderer {

	private MapView renderView;

	public void render() {
		double u = (renderView.getMinX() / renderView.textureSize) % 1.0;
		double v = (renderView.getMinZ() / renderView.textureSize) % 1.0;
		double w = renderView.getWidth() / renderView.textureSize;
		double h = renderView.getHeight() / renderView.textureSize;
	}

}
