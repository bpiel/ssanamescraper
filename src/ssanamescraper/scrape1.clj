(ns ssanamescraper.scrape1
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]))

(def ^:dynamic *base-url* "http://news.ycombinator.com/")

(defn getYearListSource []   
  (client/post "http://www.ssa.gov/cgi-bin/popularnames.cgi" 
               {:form-params
                {:year 2002
                :top 1000
                :number "p"}}))

(defn getYearList []
  (filter 
    #(and (= 1 (-> % :content count)) (= nil (:attrs %)))
    (html/select 
      (html/html-resource 
        (java.io.StringReader. 
          (:body (getYearListSource)))) 
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