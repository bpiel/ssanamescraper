(ns ssanamescraper.core
  (:require [ssanamescraper.scrape1 :refer :all]
            [ssanamescraper.trainingsetter :refer :all]))

(defn -main
  "I don't do a whole lot."
  [& args]
  (doseq [year (range 1880 1879 -1)]
    (print (renderInsertSQL (organize (getYearList (getYearListSource year))) year)))
  ;(doseq [name (getListOfNames)]
  ;  (println (toCSV (getTrainingPoint (getHistoryForName name) 2012))))
  ;(doseq [name (getListOfNames :year 2012 :gender 1)]
  ;  (println (toCSV (getTrainingPoint (getHistoryForName name 1) 2013))))
)
