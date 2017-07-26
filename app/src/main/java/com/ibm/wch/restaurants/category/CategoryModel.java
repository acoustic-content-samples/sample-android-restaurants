/**
 * Copyright IBM Corp. 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.wch.restaurants.category;

/**
 * Representation of a single category.
 */
public class CategoryModel {
    private String id;
    private String rev;
    private String name;
    private String classification;
    private String taxonomy;
    private String parent;
    private String[] ancestorIds;
    private String[] namePath;

    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String[] getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(String[] ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

    public String[] getNamePath() {
        return namePath;
    }

    public void setNamePath(String[] namePath) {
        this.namePath = namePath;
    }

    public String toString() {
        return name;
    }
}
