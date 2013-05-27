(ns ssanamescraper.scrape1
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [clojure.string :as str]))

(defn getYearListSource [year]
  (client/post "http://www.ssa.gov/cgi-bin/popularnames.cgi" 
               {:form-params
                {:year year
                :top 1000
                :number "p"}}))

(defn getYearList [source]
  (filter 
    #(and (= 1 (-> % :content count)) (= nil (:attrs %)))
    (html/select 
      (html/html-resource 
        (java.io.StringReader. 
          (:body source))) 
            [:tr :td])))

(defn organize [raw]  
  (mapcat
    #(identity (list
                 (list 0 
                       (first %)
                       (second %)
                       (nth % 2)
                       )
                 (list 1
                       (first %)
                       (nth % 3)
                       (nth % 4))))
    (partition 5 
             (map #(-> % :content first) raw)
  )))

(defn renderInsertSQL [organized year]
  (str/join "\n" (map #(format 
    "INSERT INTO `baby_name_popularity` (`year`, `name`, `gender`, `rank`, `percentage`) VALUES (%d, '%s', %s, %s, %s);"
    year, (nth % 2), (nth % 0), (nth % 1), (str/replace (nth % 3) #"%" ""))
    organized)))

(defn getDataForYearAndRenderInsertSQL [year]
  (renderInsertSQL (organize (getYearList (getYearListSource year))) year))