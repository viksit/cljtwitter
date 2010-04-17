; http://p.hagelb.org/http-client-send-body
; http://p.hagelb.org/couch.clj.html

(ns cljtwitter
   (:gen-class)
   (:use [clojure.contrib.sql :only (with-connection with-query-results)] )
   (:use [clojure.contrib.http.agent :only [http-agent status string]])
   (:use [clojure.contrib.duck-streams :only (slurp*)])
   ;(:use [clojure.http.client :only [url-encode]])
   (:use [clojure.contrib.str-utils :only [str-join]])
   (:use [org.danlarkin.json :only [encode-to-str decode-from-str]])
   (:use [clojure.contrib.str-utils :only [str-join]])
   (:import (java.sql DriverManager)
	    (java.net URL URLEncoder)
	    (java.io StringReader InputStream)))

; Variable declarations
; Database specific
(def +db-path+  "/Users/viksit/work/clojure/cljtwitter/test1.db")
(def +db-specs+ {:classname  "org.sqlite.JDBC",
                 :subprotocol   "sqlite",
                 :subname       +db-path+})
(def +transactions-query+ "select * from my_table")

; URL
(defn url-encode
  "Wrapper around java.net.URLEncoder returning a (UTF-8) URL encoded
representation of text."
  [text]
  (URLEncoder/encode text "UTF-8"))


(def +url-to-fetch+ "http://search.twitter.com/search.json?q=%s")

(defn construct-url [qy]
  "Construct a url given a query"
     (format +url-to-fetch+ (url-encode qy)))

(defn- fetch-url [url]
  "Fetch a URL through an HTTP agent"
	 (http-agent url))
	 
(defn- collect-response [& agnts]
  "Fetch the response from http agents"
  (apply await agnts)
  (for [a agnts]
    (if (= (status a) 200)
      (string a)
      (status a))))

(defn- twitter-search [query]
  "Search twitter for a given query"
  (let [agnts (fetch-url (construct-url query))]
    (collect-response agnts)))

(defn- parse-results
  "Function to parse json results returned by the search"
  [jsonres]
  (decode-from-str (first jsonres)))
  

(defn -main [& args]
  "This is a sample comment"
  (prn "Test twitter program with Sqlite")
  (prn (twitter-search "#chirp")))
  ;(with-connection +db-specs+
  ;  (with-query-results results [+transactions-query+]
  ;    ;; results is an array of column_name -> value maps
  ;    (doall (map #(println %) results))))
