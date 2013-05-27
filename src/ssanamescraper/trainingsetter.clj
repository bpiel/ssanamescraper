(ns ssanamescraper.trainingsetter
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [clojure.string :as str]))

(defdb db (mysql  {:db "bill"
                   :user "root"
                   :password "root"
                   :host "192.168.56.1"
                   :port "3306"
                   :delimiters ""}))

(defentity namepop
  (table :baby_name_popularity) 
  (database db) 
  (entity-fields :year :name :gender :percentage))

(defn getListOfNames [& {:keys [year gender] 
                         :or {year nil gender nil}}]
  (let [wheremap (into {} (filter 
                            (comp not nil? val) 
                            {:year year :gender gender}))]
  (map :name (select namepop 
                     (fields :name) 
                     (aggregate (max :percentage) :p)
                     (group :name) 
                     (order :p :desc) 
                     (where wheremap)))))




(defn getHistoryForName [name gender]
  (let [results (select namepop 
          (where {:name name
                  :gender gender}))]
    (zipmap 
      (map :year results)
      (map :percentage results))))

(defn getTrainingPoint [history year]
  (map #(or (get history %) 0) [year 
                         (- year 1) 
                         (- year 2) 
                         (- year 4)
                         (- year 8)]))

(defn toCSV [row]
  (str/join "," row))