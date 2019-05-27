(ns demo.core
  (:require [reagent.core :as r]
            [wise.router :as wr]))

(defn ysz []
  (*print-fn* 1))

(defn xue [i]
  (fn []
    (if @i
      [:a.nav-link {:href "#/asdas"} "12312"]
      [:a.nav-link {:href "#/a-items/item-112"} "abc"])
    ))

(defn zzzzz [i]
  (fn []
    ;(let [ab (r/atom i)]
    (*print-fn* @i)
    [:p "absd123123" @i [xue i]
     ]))

(defmethod wr/page-contents :four-o-four []
  ;(fn []
  [:span.main
   [:h1 "Welcome to routing-example"]
   [:div "wewewew"]

   [:p "Using "
    [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
    [:a {:href "https://github.com/juxt/bidi"} "Bidi"] ", "
    [:a {:href "https://github.com/venantius/accountant"} "Accountant"] " & "
    [:a {:href "https://github.com/PEZ/clerk"} "Clerk"]
    ". Find this example on " [:a {:href "https://github.com/PEZ/reagent-bidi-accountant-example"} "Github"]]])

(defmethod wr/page-contents :index []
  ;(fn []
  [:span.main
   [:h1 "Welcome to routing-example"]
   [:div "撒大的是"]

   [:p "Using "
    [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
    [:a {:href "https://github.com/juxt/bidi"} "Bidi"] ", "
    [:a {:href "https://github.com/venantius/accountant"} "Accountant"] " & "
    [:a {:href "https://github.com/PEZ/clerk"} "Clerk"]
    ". Find this example on " [:a {:href "https://github.com/PEZ/reagent-bidi-accountant-example"} "Github"]]])


(def app-routes-a
  ["about-a" {""               :index
              "/a-items"       {""                  :a-items
                                ["/item-" :item-id] :a-item}
              "/b-items"       {""                  :b-items
                                ["/item-" :item-id] :b-item}
              "/about"         :about
              "/missing-route" :missing-route
               true            :four-o-four}])

(def app-routes-b
  ["about-b" {""               :index
              "/a-items"       {""                  :a-items
                                ["/item-" :item-id] :a-item}
              "/b-items"       {""                  :b-items
                                ["/item-" :item-id] :b-item}
               "about"         :about
               "missing-route" :missing-route
               true            :four-o-four}])

(defn error []
  [:div
   [:ul
    [:li [:a {:href "#/"} "error-home"]]
    [:li [:a {:href "#/items/item-112"} "error-items"]]]])

(defn home []
  [:div
   [:ul
    [:li [:a {:href "#/items/item-112"} "home-items"]]
    [:li [:a {:href "#/about-a"} "home-other-a"]]
    [:li [:a {:href "#/about-b"} "home-other-b"]]
    [:li [:a {:href "#/xxx/yyy"} "home-error"]]]])

(def app-routes
  ["/" [["" home]
        ["items" {""                  :items
                  ["/item-" :item-id] :item}]
        [true error]]])

(defmethod wr/page-contents :item [param]
  [:span.main
   [:h1 "Welcome to item"]
   [:div (str "" @param)]
   [:ul
    [:li [:a {:href (wr/path-for app-routes home)} "items homed"]]
    [:li [:a {:href (wr/path-for app-routes :items)} "items"]]
    [:li [:a {:href (wr/path-for app-routes :item {:item-id "yyyy"})} "item yyyy"]]
    [:li [:a {:href (wr/path-for app-routes :item {:item-id "yyyy"} {:abc "nnnn" :bcd ["3" "4"]})} "item yyyy query"]]]])

(defmethod wr/page-contents :items []
  [:span.main
   [:h1 "Welcome to items"]
   [:ul
    [:li [:a {:href (wr/path-for app-routes home)} "items home"]]
    [:li [:a {:href (wr/path-for app-routes :item {:item-id "xxxx"})} "item xxxx"]]
    [:li [:a {:href (wr/path-for app-routes :item {:item-id "xxxx"} {:abc "mmmm" :bcd ["1" "2"]})} "item xxxx query"]]]])

(defn main []
  [:div "main-page" [wr/dispatch-router!]])

(defn ^:export init []
  (wr/start-router! app-routes app-routes-a app-routes-b)
  (r/render [main] (.getElementById js/document "app")))

(init)
