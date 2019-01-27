package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class ImportState {

    private boolean processing;
    private float progress;

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
