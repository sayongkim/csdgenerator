CSD Generator
=============

CSD Generator는 Eclipse Plugin 입니다.

# 기능

- 텀플릿을 이용해서 Controller, Service, Dao, JSP (목록, 등록, 상세) 파일을 생성합니다.
- 템플릿을 이용해서 Test (Controller, Service, Dao) 파일을 생성합니다.
- Data Source Explorer에서 Database Connection이 등록 되어 있으면 Database Table을 이용해서 MyBatis Mapper, VO를 생성할 수 있습니다. (Oracle, MySQL, PostgreSQL 지원)
- Project 설정에서 MyBatis 설정파일이 등록되어 있으면 VO 생성 시 자동으로 Alias를 등록합니다.

# 목차

* CSD Generator 실행(user-content-CSD-Generator-실행)
* Project 설정
  + General Tab
    - Template Group
  + Test Tab
    - Test Template Group
  + Controller Tab
    - Controller Template
  + Service Tab
    - Service Template
  + Dao Tab
    - Dao Template
  + Mapper Tab
    - Mapper Template
  + VO Tab
    - Type Mapping
  + JSP Tab
    - JSP Template
* CSD Function Generator 실행
* Dao Function Templates
* Service Function Templates
* Mapper Function Templates
* Template 예제
  + Prefix
  + Company, Author, Date
  + Parameter, Return
  + Database Table Column, indexColumns, Comment
  


## CSD Generator 실행

**Package Explorer**에서 Class파일이 생성될 위치를 선택 후 오른쪽 마우스로 팝업메뉴에서 **CSD Generator**를 실행합니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_17.png">

* Template: General Tab에 등록된 Template Group이 표시됩니다. 템플릿을 선택합니다.
* Database Connectoin: Data Source Explorer에 등록된 Database Connection이 표시됩니다. Database Connection을 선택하면 Tables:에 Database Table이 출력됩니다.
* Create parent location: Package Explorer에서 선택된 위치의 상단에 생성할 것인지 선택합니다.
* Create folder: Prefix로 폴더를 생성할 것인지 선택합니다.
* Controller: Controller 파일을 생성할 것인지 선택합니다.
* Service: Service 파일을 생성할 것인지 선택합니다.
* Dao: Dao 파일을 생성할 것인지 선택합니다.
* Mapper: Mapper 파일을 생성할 것인지 선택합니다. Project설정에서 Mapper 생성에 체크가 되어 있어야 하며 Database Table을 선택해야 활성화 됩니다.
* VO: VO 파일을 생성할 것인지 선택합니다. Project설정에서 VO 생성에 체크가 되어 있어야 하며 Database Table을 선택해야 활성화 됩니다.
* Superclass: VO 파일을 생성 시 부모클래스를 상속할 것인지 선택합니다.
* JSP: JSP파일을 생성할 것인지 선택합니다.
* Test Tempate: Test Tab에 등록된 Test Template Group이 표시되며 Test 생성에 체크가 되어 있어야 활성화 됩니다. 템플릿을 선택합니다.
* Test Controller: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Controller 파일을 생성할 것인지 선택합니다.
* Test Service: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Service 파일을 생성할 것인지 선택합니다.
* Test Dao: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Dao 파일을 생성할 것인지 선택합니다.
* Vo Superclass: VO 생성 시 상속받을 부모클랙스를 선택합니다.
* Parameter: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Parameter Type을 선택합니다.
* Return: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Return Type을 선택합니다.
* Prefix: 폴더 및 파일명의 기준이 됩니다.
* Regular expressions: Tables을 여러개 선택 시 정규식으로 Prefix를 변경할 수 있습니다.
* Preview: 생성될 폴더 및 파일을 미리 볼 수 있습니다.
* Tables: Database Connection에서 선택된 Database Table이 표시됩니다. 생성할 Table을 선택합니다.

## Project 설정

### General Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_01.png">

* Company: 회사명을 설정합니다.
* Author: 작성자를 설정합니다.
* Database Connection: 기본 Database Connection을 설정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

###  Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_02.png" width="51%" height="51%" ></div>

  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.
  - Mapper Tempate: MyBatis Mapper 템플릿을 선택합니다.
  - JSP Template: JSP 템플릿을 선택합니다.

## Test Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_03.png">

* Create test: 테스트파일의 생성여부를 설정합니다.
* Create test controller folder: Test Controller 폴더의 생성여부를 설정합니다.
* Create test service folder: Test Service 폴더 생성여부를 설정합니다.
* Create test dao folder: Test Dao 폴더 생성여부를 설정합니다.
* Test Path: 테스트파일이 생성될 위치를 지정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

###  Test Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_04.png" width="51%" height="51%" ></div>

  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.

## Controller Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_05.png">

기준 Controller 폴더명은 **Controller**입니다.

* Create controller folder: Controller 폴더의 생성여부를 설정합니다.
* Add prefix controller folder name: Prefix를 Controller 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestController
* Create controller sub folder: Controller폴더 하위에 폴더생성여부를 설정합니다.
* Controller Template
  - Add Template... : Controller Template 등록창을 호출합니다.
  - Edit... : Controller Template 수정창을 호출합니다.
  - Remove : 선택된 Controller Template을 삭제합니다.

###  Controller Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_06.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
## Service Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_07.png">

기준 Servie 폴더명은 **Service**입니다.

* Create service folder: Service 폴더의 생성여부를 설정합니다.
* Add prefix service folder name: Prefix를 Service 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestService
* Create service sub folder: Service폴더 하위에 폴더생성여부를 설정합니다.
* Create ServiceImpl: Service Impl 파일의 생성여부를 설정합니다.
* Create ServiceImpl folder: Service Impl 폴더의 생성여부를 설정합니다.
* Service Template
  - Add Template... : Service Template 등록창을 호출합니다.
  - Edit... : Service Template 수정창을 호출합니다.
  - Remove : 선택된 Service Template을 삭제합니다.

###  Service Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_08.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
## Dao Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_09.png">

기준 Dao 폴더명은 **Dao**입니다.

* Create dao folder: Dao 폴더의 생성여부를 설정합니다.
* Add prefix dao folder name: Prefix를 Dao 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestDao
* Create dao sub folder: Dao폴더 하위에 폴더생성여부를 설정합니다.
* Dao Template
  - Add Template... : Dao Template 등록창을 호출합니다.
  - Edit... : Dao Template 수정창을 호출합니다.
  - Remove : 선택된 Dao Template을 삭제합니다.

###  Dao Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_10.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.

## Mapper Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_11.png">

* Create mapper folder: MyBatis Mapper 파일의 생성여부를 설정합니다.
* Mapper Path: MyBatis Mapper 파일이 생성될 폴더를 지정합니다.
* Mapper Template
  - Add Template... : Mapper Template 등록창을 호출합니다.
  - Edit... : Mapper Template 수정창을 호출합니다.
  - Remove : 선택된 Mapper Template을 삭제합니다.

###  Mapper Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_12.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.

## VO Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_13.png">

* Create Vo: VO의 생성여부를 설정합니다.
* Create Search Vo: 목록등에 사용될 수 있는 검색용 VO의 생성여부를 설정합니다.
* Create vo folder: VO 폴더의 생성여부를 설정합니다.
* Vo Folder Name: VO 폴더명을 설정합니다.
* Vo Path: VO 파일이 생성될 위치를 지정합니다.
* Superclass: 상속받을 Super Class를 선택합니다.
* MyBatis Setting File: MyBatis설정파일을 지정합니다.
* Type Mapping
  - Add Template... : Type Mapping 등록창을 호출합니다.
  - Edit... : Type Mapping 수정창을 호출합니다.
  - Remove : 선택된 항목을 삭제합니다.

###  Type Mapping

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_14.png" width="51%" height="51%" ></div>

  - Data Type: Database Type을 입력합니다.
  - Java Object: Java Object를 선택합니다.

지원되는 Java Object는 다음과 같습니다.

|Java Object|
|:-----------:|
|String|
|BigDecimal|
|Date|
|Timestamp|
|boolean|
|char|
|byte|
|short|
|int|
|long|
|float|
|double|

## JSP Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_15.png">

* Create jsp folder: JSP파일의 생성여부를 설정합니다.
* JSPPath: JSP파일이 생성될 폴더를 지정합니다.
* JSPTemplate
  - Add Template... : JSP Template 등록창을 호출합니다.
  - Edit... : JSP Template 수정창을 호출합니다.
  - Remove : 선택된 JSP Template을 삭제합니다.

###  JSP Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_16.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - List Template File: JSP 목록 템플릿파일을 선택합니다.
  - Post Template File: JSP 등록 템플릿파일을 선택합니다.
  - View Template File: JSP 상세 템플릿파일을 선택합니다.

## CSD Function Generator 실행

**Package Explorer**에서 **Service*.java* 파일이나 **ServiceImpl.java**파일을 선택 후 오른쪽 마우스로 팝업메뉴에서 **CSD Function Generator**를 실행합니다.

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_18.png" width="80%" height="80%" ></div>

* Database Connectoin: Data Source Explorer에 등록된 Database Connection이 표시됩니다. Database Connection을 선택하면 Tables:에 Database Table이 출력됩니다.
* Tables: Database Connection에서 선택된 Database Table이 표시됩니다. 생성할 Table을 선택합니다.
* Service: Function을 Service에 추가할 것인지 선택합니다.
* Dao: Function을 Dao 추가할 것인지 선택합니다.
* Mapper: Mapper에 추가할 것인지 선택합니다.
* VO: VO 파일을 생성할 것인지 선택합니다. Project설정에서 VO 생성에 체크가 되어 있어야 하며 Database Table을 선택해야 활성화 됩니다.
* Superclass: VO 파일을 생성 시 부모클래스를 상속할 것인지 선택합니다.
* Select Count: 템플릿에 등록된 Select Count를 추가할 것인지 선택합니다.
* Select List: 템플릿에 등록된 Select List를 추가할 것인지 선택합니다.
* Select One: 템플릿에 등록된 Select One를 추가할 것인지 선택합니다.
* Insert: 템플릿에 등록된 Insert를 추가할 것인지 선택합니다.
* Update: 템플릿에 등록된 Update를 추가할 것인지 선택합니다.
* Delete: 템플릿에 등록된 Delete를 추가할 것인지 선택합니다.
* Vo Superclass: VO 생성 시 상속받을 부모클랙스를 선택합니다.
* Parameter: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Parameter Type을 선택합니다.
* Return: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Return Type을 선택합니다.
* Prefix: 폴더 및 파일명의 기준이 됩니다.
* Service Interface: Function을 추가할 Service를 선택합니다.
* Dao: Function을 추가할 Dao를 선택합니다.

## Dao Function Templates

CSD Function Generator에서 Dao Template을 설정합니다.

**Select Count**, **Select List**, **Select One**, **Insert**, **Update**, **Delete** 으로 구성되어 있습니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_19.png">

## Service Function Templates

CSD Function Generator에서 Service Template을 설정합니다.

**Select Count**, **Select List**, **Select One**, **Insert**, **Update**, **Delete** 으로 구성되어 있습니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_20.png">

## Mapper Function Templates

CSD Function Generator에서 Mapper Template을 설정합니다.

**Select Count**, **Select List**, **Select One**, **Insert**, **Update**, **Delete** 으로 구성되어 있습니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_21.png">

## Template 예제

### Prefix

CSD Generator 창에서 입력한 Prefix는 소스에서 카멜표기법으로 변환 후 **[prefix]**를 대체합니다. **[capitalizePrefix]**는 Prefix를 파스칼표기법으로 변환 후 대체합니다.

Prefix에 **test**를 입력했을 경우 예제입니다.

```java
public class [capitalizePrefix]Controller {

	/**
	 * Desc : 목록 조회
	 * @Method : select[capitalizePrefix]List
	 * @return
	 */
	@RequestMapping(value="/[prefix]/list", method=RequestMethod.GET)
	public ModelAndView select[capitalizePrefix]List() {

		ModelAndView modelAndView = new ModelAndView("[prefix]/[prefix]");

		return modelAndView;
	}
	
}
```

```java
public class TestController {

	/**
	 * Desc : 목록 조회
	 * @Method : selectTestList
	 * @return
	 */
	@RequestMapping(value="/test/list", method=RequestMethod.GET)
	public ModelAndView selectTestList() {

		ModelAndView modelAndView = new ModelAndView("test/test");

		return modelAndView;
	}
	
}
```

### Company, Author, Date

Project 설정에서 등록한 Comapny, Author 는 **[company]**, **[author]**를 대체하며 **[date]**는 생성된 날짜를 대체합니다.

```java
	/**
	 * Desc : 목록 조회
	 * @Company : [company]
	 * @Author : [author]
	 * @Date : [date]
	 * @return
	 */
```

```java
	/**
	 * Desc : 목록 조회
	 * @Company : 회사명
	 * @Author : 작성자명
	 * @Date : 2016. 08. 05
	 * @return
	 */
```

### Parameter, Return

CSD Generator 창에서 선택한 Parameter, Return으로 **[searchParamType]**, **[searchParamName]**, **[paramType]**, **[paramName]**, **[returnType]** 을 대체합니다.

CSD Generator 창에서 Parameter, Return을 **HashMap**으로 선택했을 경우 예제입니다.

```java
	public [returnType] selectTest([searchParamType] [searchParamName]) {
		return TestDao.selectTest([searchParamName]);
	}
```
```java
	public HashMap<String, Object> selectTest(HashMap<String, Object> searchMap) {
		return TestDao.selectTest(searchMap);
	}
```

### Database Table Column, indexColumns, Comment

CSD Generator 창에서 Database Table을 선택 시 MyBatis Mapper 에서 column, indexColumns를 자동으로 생성합니다.
**[table]**는 선택된 Database Table로 대체하며 **[paramType]**, **[resultType]** 은 CSD Generator 창에서 선택한 Parameter, Return으로 대체합니다.

**NOTICE** 라는 테이블을 선택 시 예제입니다.
|Column Name|Primary Key|Comment|
|:---------:|:---------:|:---------:|
|NO_SEQ|O|번호|
|TITLE||제목|
|CONTENTS||내용|
|REG_NAME||작성자|
|REG_DATE||작성일|

* MyBatis Mapper - select

```xml
	<!--
	Desc : 상세 조회
	TABLE : [table]
	-->
	<select id="selectTestList" parameterType="[paramType]" resultType="[resultType]">
		SELECT
			[columns]
		FROM [table]
		WHERE 1=1 
		[indexColumns]
	</select>
```
```xml
	<!--
	Desc : 상세 조회
	TABLE : NOTICE
	-->
	<select id="selectTestList" parameterType="hashMap" resultType="hashMap">
		SELECT
			NO_SEQ AS noSeq
			,TITLE AS title
			,CONTENTS AS contents
			,REG_NAME AS regName
			,REG_DATE AS regDate
		FROM NOTICE
		WHERE 1=1 
		AND NO_SEQ = #{noSeq}
	</select>
```
* MyBatis Mapper - insert

```xml
	<insert id="insertTest" parameterType="[paramType]">
		INSERT INTO [table] (
			[columns]
		) VALUES (
			[values]
		)
	</insert>
```

```xml
	<insert id="insertTest" parameterType="hashMap">
		INSERT INTO NOTICE (
			NO_SEQ
			,TITLE
			,CONTENTS
			,REG_NAME
			,REG_DATE
		) VALUES (
			#{noSeq}
			,#{title}
			,#{contents}
			,#{regName}
			,#{regDate}
		)
	</insert>
```

* MyBatis Mapper - update

```xml
	<update id="updateTest" parameterType="[paramType]">
		UPDATE [table] SET
			[columns]
		WHERE 1=1
			[indexColumns]
	</update>
```

```xml
	<update id="updateTest" parameterType="hashMap">
		UPDATE NOTICE SET
			NO_SEQ = #{noSeq}
			,TITLE = #{title}
			,CONTENTS = #{contents}
			,REG_NAME = #{regName}
			,REG_DATE = #{regDate}
		WHERE 1=1
			AND NO_SEQ = #{noSeq}
	</update>
```

* JAVA - 반복

JAVA에서 반복은 **/\*r:s\*/**다음줄부터 **/\*r:e\*/**이전줄까지의 내용이 반복됩니다. 내용에는 **[column]**, **[comment]**가 지원됩니다.

```
		/*r:s*/
		resultMap.get('[column]'); // [comment]
		/*r:e*/
```

```
		/*r:s*/
		resultMap.get('regSeq'); // 번호
		resultMap.get('title'); // 제목
		resultMap.get('contents'); // 내용
		resultMap.get('regName'); // 작성자
		resultMap.get('regDate'); // 작성일
		/*r:e*/
```

* JSP - 반복

JSP에서 반복은 **<!--r:s-->**다음줄부터 **<!--r:e-->**이전줄까지의 내용이 반복됩니다. 내용에는 **[column]**, **[comment]**가 지원됩니다.

```
    <!--r:s-->
    <input type="text" name="[column]" placeholder="[comment]">
    <!--r:e-->
```

```
    <!--r:s-->
    <input type="text" name="noSeq" placeholder="번호">
    <input type="text" name="title" placeholder="제목">
    <input type="text" name="contnets" placeholder="내용">
    <input type="text" name="regName" placeholder="작성자">
    <input type="text" name="regDate" placeholder="작성일">
    <!--r:e-->
```
