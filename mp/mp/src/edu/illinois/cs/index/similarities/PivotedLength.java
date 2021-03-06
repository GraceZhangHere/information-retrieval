package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class PivotedLength extends SimilarityBase {
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
        float s = 0.75f;
        return (float)((1.0 + Math.log(1.0 + Math.log(termFreq))) / (1.0 - s + s * docLength / stats.getAvgFieldLength())
                * 1.0
                * Math.log((stats.getNumberOfDocuments() + 1.0) / stats.getDocFreq()));
    }

    @Override
    public String toString() {
        return "Pivoted Length Normalization";
    }

}
