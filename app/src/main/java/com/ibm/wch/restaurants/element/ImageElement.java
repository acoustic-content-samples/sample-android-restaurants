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

package com.ibm.wch.restaurants.element;

import java.util.Map;

/**
 * Representation of image element.
 */
public class ImageElement extends AbstractElement {
    private Map<String, Rendition> renditions;

    public Map<String, Rendition> getRenditions() {
        return renditions;
    }

    public void setRenditions(Map<String, Rendition> renditions) {
        this.renditions = renditions;
    }

    public boolean hasRendition(String name) {
        return renditions != null && renditions.containsKey(name);
    }

    public Rendition getRendition(String name) {
        return renditions.get(name);
    }
}
