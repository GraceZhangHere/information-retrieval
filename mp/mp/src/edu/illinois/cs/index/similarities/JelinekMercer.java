package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.util.MathUtil;

public class JelinekMercer extends LMSimilarity {

    private LMSimilarity.DefaultCollectionModel model; // this would be your reference model
    private float queryLength = 0; // will be set at query time automatically

    public JelinekMercer() {
        model = new LMSimilarity.DefaultCollectionModel();
    }

    /**
     * Returns a score for a single term in the document.
     *
     * @param stats
     *            Provides access to corpus-level statistics
     * @param termFreq
     * @param docLength
     */
    @Override
    protected float score(BasicStats stats, float termFreq, float docLength) {
        float lambda = 0.1f;
        return (float)MathUtil.log(10.0,(((1.0 - lambda) * termFreq / docLength + lambda * model.computeProbability(stats))
                / (lambda * model.computeProbability(stats))));
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return "Jelinek-Mercer Language Model";
    }

    public void setQueryLength(float length) {
        queryLength = length;
    }

}
