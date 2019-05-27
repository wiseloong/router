(ns wise.router
  (:require [reagent.core :as r]
            [bidi.bidi :as bidi]
            [clojure.string :as cstr]
            [goog.string :as gstr]
    ;[goog.uri.utils :as guu]
            [goog.events :as ge])
  (:import goog.History
           goog.history.EventType))

(def ^:private !location (r/atom {}))

;; 根据url地址获取?后面的参数
(defn- string-query [uri]
  (let [s (-> (cstr/split uri #"\?" 2) second)]
    (when-not (cstr/blank? s)
      (letfn [(key-fn [[k v]] {(keyword k) v})
              (param-map [s]
                (-> (cstr/split s #"=" 2)
                    key-fn))
              (merge-list [m e]
                (if (sequential? m)
                  (conj m e)
                  [m e]))]
        (as-> (gstr/urlDecode s) m
              (cstr/split m #"&")
              (map param-map m)
              (apply merge-with merge-list m))))))

;; 根据参数拼接得到?后面的url地址
(defn- query-string [query-params]
  (let [enc (fn [a b] (str (if (keyword? a) (name a) a) "=" (gstr/urlEncode (str b))))
        join (fn [v] (apply str (interpose "&" v)))]
    (join
      (map (fn [[k v]]
             (if (sequential? v)
               (join (map enc (repeat k) v))
               (enc k v)))
           query-params))))

(defmulti page-contents (fn [_ x] x))

(defn path-to "跳转url，可用于按钮的点击事件"
  [url] (set! (.-href js/location) url))

(defn path-for "获取url地址"
  ([route handler]
   (path-for route handler nil nil))
  ([route handler route-params]
   (path-for route handler route-params nil))
  ([route handler route-params query-params]
   (when (nil? handler)
     (throw (ex-info "Cannot form URI from a nil handler" {})))
   (let [path (bidi/unmatch-pair route {:handler handler :params route-params})
         query (when query-params (str "?" (query-string query-params)))]
     (str "#" path query))))

(defn dispatch-router! []
  (fn []
    (let [handler (:handler @!location)
          params (r/atom (dissoc @!location :handler))]
      (condp #(%1 %2) handler
        keyword? (page-contents params handler)
        fn? [handler params]
        [:div "错误页面！没有此页面，请联系管理员！"]))))

(defn merge-router [[k1 v1] r2]
  (condp #(%1 %2) v1
    map? [k1 (merge {} r2 v1)]
    vector? (let [f (pop v1)
                  l (peek v1)]
              [k1 (conj f r2 l)])
    [k1 v1]))

(defn- merge-router! [route coll]
  (if coll
    (let [routes (merge-router route (first coll))]
      (recur routes (next coll)))
    route))

(defprotocol Router
  (set-location! [_ location])
  (path-for! [_ location]))

(defonce history (History.))

(defn start-router!
  "单页面路由跳转，示例如下：
  Starts up a Bidi router based on Google Closure's 'History'
  Example usage:
   (ns comp.core
     (:require [wise.router :as wr]))

   (defn home-page []
     [:div \"home-page\"
       [:a {:href \"#/about\"} \"about-page\"]])

   (defn a-page [item-id]
     (println @item-id)
     [:div \"a-page\"
       [:a {:href \"#/\"} \"home-page\"]])
   ...

   (def app-routes
     [\"/\" {\"\"        home-page
             \"a-items\" {\"\"                  a-pages
                          [\"/item-\" :item-id] a-page}
             \"about\"   about-page
             true        error-page}])

   (defn main []
     [:div [wr/router-page!]]))

   (defn init! []
     (wr/router! app-routes)
     (r/render [main] (.getElementById js/document \"app\"))

   (init!)"
  [route & coll]
  (let [routes (merge-router! route coll)]
    (.setEnabled history true)
    (letfn [(token->location [token]
               (let [route (bidi/match-route routes token)
                     query-params (string-query token)]
                 (if query-params
                   (merge {:query-params query-params} route)
                   route)))
             (location->token [{:keys [handler route-params query-params]}]
               (let [token (bidi/unmatch-pair routes {:handler handler
                                                      :params  route-params})]
                 (if query-params
                   (str token "?" (query-string query-params))
                   token)))]
      (ge/listen history EventType.NAVIGATE
                 (fn [e] (reset! !location (token->location (.-token e)))))
      (let [initial-token (let [token (.getToken history)]
                             (if (cstr/blank? token)
                               (do (.replaceToken history "/") "/")
                               token))]
        (reset! !location (token->location initial-token))
        (reify Router
           (set-location! [_ location]
             (.setToken history (location->token location)))
           (path-for! [_ location]
             (str "#" (location->token location))))))))

#_(defn start-router!
    "这种方式比上面router!更灵活，可以给独立的组件使用，比如认证组件中的登陆登出页面；可以扩展Router方法，实现更多功能。
      Starts up a Bidi router based on Google Closure's 'History'
      Example usage:
       (ns comp.core
         (:require [wise.router :as wr]))

       (defn home-page []
         [:div \"home-page\"
           [:a {:href \"#/about\"} \"about\"]])

       (defn a-page [item-id]
         (println @item-id)
           [:div \"a-page\"
             [:a {:href \"#/\"} \"home-page\"]])
       ...

       (def app-routes
         [\"/\" {\"\"        home-page
                 \"a-items\" {\"\"                  a-pages
                              [\"/item-\" :item-id] a-page}
                 \"about\"   about-page
                 true        error-page}])

       (defn main [r]
         [:div [wr/router-pages! r]]))

       (defn init! []
         (let [router (wr/start-router! app-routes)]
           (r/render [main router] (.getElementById js/document \"app\")))

       (init!)"
    [routes]
    (let [!loc (r/atom {})]
      (start-router routes !loc)))

