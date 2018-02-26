package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class OkapiBM25 extends SimilarityBase {
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
        float k1 = 1.2f;
        float k2 = 750f;
        float b = 0.75f;
        return (float) (Math.log((stats.getNumberOfDocuments()-stats.getDocFreq()+0.5)/(stats.getDocFreq()+0.5))
                        *(((k1+1)*termFreq)/(k1*(1-b+b*docLength/stats.getAvgFieldLength())+termFreq))
                        *((k2+1)*1/(k2+1)));


//        float s = 0.22f;
//        return (float)(1.0 * (termFreq/(termFreq + s + (s*docLength)/stats.getAvgFieldLength()))
//                *(float)Math.log((stats.getNumberOfDocuments()+1)/stats.getDocFreq()));
    }

    @Override
    public String toString() {
        return "Okapi BM25";
    }

}
