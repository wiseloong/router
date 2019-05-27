# router

#### 介绍
wiseloong-前端-路由

#### 软件架构
在bidi基础上升级，支持query-params。

`[wiseloong/router "0.1.0-SNAPSHOT"]`，使用前需要`lein install`


#### 使用教程

1. 引用，wise.router

```clojure
(:require [reagent.core :as r]
          [wise.router :as wr])
```

2. 定义路由配置，app-routes，支持`:key`（如下:items），和`fn`（如下：home）

```clojure
(def app-routes
  ["/" [["" home]
        ["items" {""                  :items
                  ["/item-" :item-id] :item}]
        [true error]]])
```

3. 页面，page-contents

```clojure
(defn error []
  [:div
   [:ul
    [:li [:a {:href "#/"} "error-home"]]
    [:li [:a {:href "#/items/item-112"} "error-items"]]]])

(defn home []
  [:div
   [:ul
    [:li [:a {:href "#/items/item-112"} "home-items"]]
    [:li [:a {:href "#/xxx/yyy"} "home-error"]]]])

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
```

> 上面的：param为`r/atom`，内容为：{:route-params {}, :query-params {}}

4. 开始，start-router!；路由页面，dispatch-router!

```clojure
(defn main []
  [:div "main-page" [wr/dispatch-router!]])

(defn ^:export init []
  (wr/start-router! app-routes)
  (r/render [main] (.getElementById js/document "app")))
```

> start-router!支持接收多个配置，把后面的配置app-routes-a放到第一个配置app-routes里的第二个元素里的倒数第二的位置，例如：

```clojure
(def app-routes-a
  ["about-a" {""               :index
              "/about"         :about}])
              
(defn ^:export init []
  (wr/start-router! app-routes app-routes-a app-routes-b)
  (r/render [main] (.getElementById js/document "app")))

;; 合并后的结果（忽略app-routes-b）
["/" [["" home]
      ["items" {""                  :items
                ["/item-" :item-id] :item}]
      ["about-a" {""               :index
              "/about"         :about}]
      [true error]]]
```

5. 获取地址字符串，path-for

``` clojure
[:a {:href (wr/path-for app-routes home)} "items homed"]
=>
"#/"

[:a {:href (wr/path-for app-routes :item {:item-id "xxxx"} {:abc "mmmm" :bcd ["1" "2"]})} "item xxxx query"]]]
=>
"#/items/item-xxxx?abc=mmmm&bcd=1&bcd=2"
```

6. 跳转网页：path-to

```clojure
[:button {:on-click (wr/path-to "#/items/item-xxxx?abc=mmmm&bcd=1&bcd=2")}]
```

#### 使用说明

参考代码：src/demo/core.cljs

