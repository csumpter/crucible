(ns crucible.encoding.keys-test
  (:require [crucible.encoding.keys :as keys]
            [crucible.template :as t]
            [crucible.encoding :as enc]
            [crucible.resources :as r]
            [clojure.test :refer :all]
            [clojure.spec :as s]))

(def testing-123-translation "Testing123Foo")

(defmethod keys/->key :testing-123 [_] testing-123-translation)

(s/def ::foo (s/keys :req [::testing-123]))

(def test (r/resource-factory "Test::Test" ::foo))

(deftest ->key-translates-on-encode-template
  (testing "->key in element key position translates"
    (is (= {"AWSTemplateFormatVersion" "2010-09-09"
            "Description" "t"
            "Parameters" {testing-123-translation {"Type" "String"}}}
           (cheshire.core/decode
            (enc/encode
             (t/template "t"
                         :testing-123 (t/parameter)))))))

  (testing "->key in element properties position translates"
    (is (get-in (cheshire.core/decode
                 (enc/encode
                  (t/template "t"
                              :foo (test {::testing-123 "foo"}))))
                ["Resources" "Foo" "Properties" "Testing123Foo"]))))
