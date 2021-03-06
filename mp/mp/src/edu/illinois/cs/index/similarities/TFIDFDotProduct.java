package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.util.MathUtil;

public class TFIDFDotProduct extends SimilarityBase {
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
        return (float)((1.0 + MathUtil.log(10.0,termFreq))
                * MathUtil.log(10.0,(stats.getNumberOfDocuments() + 1.0) / stats.getDocFreq()));
    }

    @Override
    public String toString() {
        return "TF-IDF Dot Product";
    }
}
