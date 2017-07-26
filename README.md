# sample-android-restaurants
This sample native Android app demonstrates how IBM Watson Content Hub APIs can be used to allow a user to search for restaurants that have been added through the Content Hub user interface.

Note that this sample uses WCH authoring APIs to search for and retrieve content and images. WCH also has delivery content and search APIs that would typically be used for a retrieval scenario such as this mobile app. The delivery APIs are very similar to the authoring APIs but are simpler to use since they don't require authentication.

This sample covers:
* Using the categories API to retrieve all categories of a taxonomy
* Using the search API to search for content items by name and category
* Using the content API to access content and its elements
* Using the content API to update contents

App features:
* Searching restaurants by name and/or cuisine type
* Listing of restaurants matching search
* Details screen of each restaurant
* Restaurant's photo using different renditions
* Restaurant's opening hours for current day
* Restaurant's distance from your current GPS location
* Opening the restaurant's website
* Opening the restaurant's location on Google Maps
* Calling the restaurant directly
* Rating the restaurant

The following are some screenshots of the app:

![Alt text](/docs/screenshot-search.png?raw=true "Search")
![Alt text](/docs/screenshot-list.png?raw=true "Restaurants listing")
![Alt text](/docs/screenshot-restaurant.png?raw=true "Restaurant details")

### Running the sample

#### 0. Prerequisites

This project assumes you have basic knowledge of developing Android apps and an IDE ready. The project requires Android 7.1.1 (API 25) so make sure you have that SDK.

#### 1. Download the files and copy into project

Download all the files onto your workstation. You should be able to open it as a project using Android Studio.

#### 2. Setup the content model manually or using [wchtools](https://github.com/ibm-wch/wchtools-cli/releases)

You will need to either manually create the following (steps 2a-2e) or import them using the [wchtools-export folder](/docs/wchtools-export/). If you are using wchtools, you can go directly to step 3 after importing.

###### 2a. Cuisine Taxonomy

Create a taxonomy named "Cuisine" and make note of the taxonomy's ID.

You may use the JSON found in [cuisine-taxonomy.json](/docs/manual-export/cuisine-taxonomy.json) to create this.

###### 2b. Restaurant Image Profile

Create an image profile named "Restaurant Renditions" with the renditions below and make note of the image profile's ID.

| Label | Key | Width | Height |
| --- | --- | --- | --- |
| mobile | mobile | 377 | 199 |
| thumbnail | thumbnail | 150 | 90 |

You may use the JSON found in [restaurant-image-profile.json](/docs/manual-export/restaurant-image-profile.json) to create this.

###### 2c. Restaurant Content Type

Create a content type named "Restaurant" with the following elements:

| Label | Key | Type | Settings |
| --- | --- | --- | --- |
| Summary | summary | Text | Required |
| Address | address | Text | Required |
| Phone | phone | Text | Required |
| Cuisine | cuisine | Category | Required, Restrict to Cuisine Taxonomy |
| Website | website | Text |  |
| Photo | photo | Image | Restaurant Renditions image profile |
| SUN Opening Hours | openingHours1 | Text |  |
| MON Opening Hours | openingHours2 | Text |  |
| TUE Opening Hours | openingHours3 | Text |  |
| WED Opening Hours | openingHours4 | Text |  |
| THU Opening Hours | openingHours5 | Text |  |
| FRI Opening Hours | openingHours6 | Text |  |
| SAT Opening Hours | openingHours7 | Text |  |
| Rating | rating | Number | Required, Type decimal, Min 0  |
| Total Ratings | totalRatings | Number | Required, Type integer, Min 0  |

You may use the JSON found in [restaurant-type.json](/docs/manual-export/restaurant-type.json) to create this. Before using the JSON, you will need to replace [ID_OF_CUISINE_TAXONOMY] with the ID of the taxonomy created in part 2a and replace [ID_OF_RESTAURANT_IMAGE_PROFILE] with the ID of the image profile created in part 2b.

###### 2d. Create some cuisines

Create some cuisines as categories inside the Cuisine taxonomy created in 2a.

You may use the JSON files found in [sample-cuisines/](/docs/manual-export/sample-cuisines) as an example. Before using the JSON files, you will need to replace [ID_OF_CUISINE_TAXONOMY] with the ID of the taxonomy created in part 2a.

###### 2e. Create some restaurants

Create some restaurants as contents using the content type created in part 2c.

You may use the JSON files found in [sample-restaurants/](/docs/manual-export/sample-restaurants) as an example. These samples require you to have created the cuisine categories from the sample in part 3. You will also need to replace [ID_OF_RESTAURANT_TYPE] with the ID of the content type created in part 2c and [ID_OF_X_CUISINE_CATEGORY] with the corresponding category ID created in part 3. For example, replace [ID_OF_JAPANESE_CUISINE_CATEGORY] with the ID of the "Japanese" cuisine category.

There are some images included in [sample-restaurants/photos/](/docs/manual-export/sample-restaurants/photos) for you to set as the restaurant photo.

#### 3. Configure Content Hub Options

Import the project files into your Android development IDE. You will need to modify the following settings inside  [WchConfig.java](/src/main/java/com/ibm/wch/restaurants/WchConfig.java):

* [ENTER YOUR USERNAME]
* [ENTER YOUR PASSWORD]
* [ENTER ID OF CUISINE TAXONOMY] with the ID of the taxonomy created in part 2a or "96ff4a8c19d6a321fa7aa6ee03f0468a" if imported using wchtools.

This sample uses a hard-coded username and password. Avoid doing when releasing to production. You could change the app to provide inputs for username and password.

#### 4. Run the App

Compile the project and run the app on your phone or emulator.
