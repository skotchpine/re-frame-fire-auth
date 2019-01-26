(ns re-frame-fire-auth.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clojure.string :as string]))

;;
;; ## Events
;;
(defn init [_ _]
  {::user nil
   ::error nil
   ::loading? false})

(defn login-with-google [db _]
  (let [provider (doto (js/firebase.auth.GoogleAuthProvider.)
                       (.addScope "profile")
                       (.addScope "email"))]
    (-> (js/firebase.auth.)
        (.signInWithPopup provider)
        (.catch #(re-frame/dispatch [::error %]))))
  (assoc db ::loading? true))

(defn auth-state-changed [db [_ user]]
  (assoc db ::loading? false
            ::error nil
            ::user user))

(defn error [db [_ err]]
  (assoc db ::loading? false
            ::error err))

(defn logout [_ _]
  (.signOut (js/firebase.auth.))
  {})

(re-frame/reg-event-db ::init init)
(re-frame/reg-event-db ::login-with-google login-with-google)
(re-frame/reg-event-db ::auth-state-changed auth-state-changed)
(re-frame/reg-event-db ::error error)
(re-frame/reg-event-fx ::logout logout)

;;
;; ## Subs
;;
(re-frame/reg-sub ::user ::user)
(re-frame/reg-sub ::error ::error)
(re-frame/reg-sub ::loading? ::loading?)

;;
;; ## Views
;;
(defn- keyed-list [coll]
  (map-indexed (fn [index item]
                 (if (vector? item)
                   (with-meta item {:key index})
                   item))
               coll))

(defn page [& content]
  [:div.vw-100.vh-100.flex.items-center.justify-around
   (keyed-list content)])

(defn card [& content]
  [:div.w5.br2.pa4.tc.bg-light-gray.shadow-5
   (keyed-list content)])

(defn heading [& content]
  [:div.f4.tc
   (keyed-list content)])

(defn section [& content]
  [:div.mt4
   (keyed-list content)])

(defn button [{:keys [disabled? color on-click label]}]
  [:div.link.br1.pa2.shadow-3
   (if disabled?
     {:class "bg-gray"}
     {:class (str "bg-" color " dim pointer")
      :on-click on-click})
   label])

(defn view []
  (let [user (re-frame/subscribe [::user])
        error (re-frame/subscribe [::error])
        loading? (re-frame/subscribe [::loading?])]
    [page
     (when-not @user
       [card
        [heading "Login"]

        (when @error
          [section
           [:div.f5.dark-red (.-message @error)]])

        [section
         [button
          {:disabled? @loading?
           :color "green"
           :on-click #(re-frame/dispatch [::login-with-google])
           :label "Log in with google"}]]])

     (when @user
       [card
        [heading
         [:img.db.center.w-5.br-100.shadow-3
          {:src (.-photoURL @user)}]
         (.-displayName @user)]

        [section
         [button
          {:disabled? @loading?
           :color "yellow"
           :on-click #(re-frame/dispatch [::logout])
           :label "Log out"}]]

        [section
         [:div.f5 "Now go buy Tyler coffee!"]]])]))

;;
;; ## Main
;;
(defonce ^:private initialized
  (atom false))

(defn ^:export mount []
  (when-not @initialized
    (reset! initialized true)

    (enable-console-print!)

    (re-frame/dispatch-sync [::init])
    (re-frame/clear-subscription-cache!)

    ;; Put your creds here
    (-> {:apiKey ""
         :authDomain ""
         :projectId ""}
        clj->js
        js/firebase.initializeApp.)

    (.onAuthStateChanged (js/firebase.auth.)
      #(re-frame/dispatch [::auth-state-changed %])
      #(re-frame/dispatch [::error %])) 

    (js/console.log "Hello, there. ~ Tyler"))

    (->> "app"
         js/document.getElementById
         (reagent/render [view])))
