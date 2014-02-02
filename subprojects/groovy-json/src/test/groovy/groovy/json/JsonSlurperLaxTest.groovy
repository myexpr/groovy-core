package groovy.json

/**
 * Created by Richard on 2/2/14.
 */
class JsonSlurperLaxTest extends JsonSlurperTest{
    public JsonSlurperLaxTest () {
        parser = new JsonSlurper().setType( JsonParserType.LAX );
    }


    void testNullEmptyMalformedPayloads() {
        shouldFail(IllegalArgumentException) { parser.parseText(null)   }
        shouldFail(IllegalArgumentException) { parser.parseText("")     }

        shouldFail(JsonException) { parser.parseText("[")           }
        shouldFail(JsonException) { parser.parseText('{"')          }
        shouldFail(JsonException) { parser.parseText("[a")           }
        shouldFail(JsonException) { parser.parseText('{a"')          }
        shouldFail(JsonException) { parser.parseText("[\"a\"")           }
        shouldFail(JsonException) { parser.parseText('{"a"')          }

    }


    void testObjectWithSimpleValues() {
        assert parser.parseText('{"a": 1, "b" : true , "c":false, "d": null}') == [a: 1, b: true, c: false, d: null]

         parser.parseText('{true}')
         shouldFail { parser.parseText('{"a"}') }
         parser.parseText('{"a":true')
         parser.parseText('{"a":}')
         shouldFail {parser.parseText('{"a":"b":"c"}') }
         parser.parseText('{:true}')
    }



    void testArrayOfArrayWithSimpleValues() {
        assert parser.parseText('[1, 2, 3, ["a", "b", "c", [true, false], "d"], 4]') ==
                [1, 2, 3, ["a", "b", "c", [true, false], "d"], 4]

        shouldFail(JsonException) { parser.parseText('[') }
        parser.parseText('[,]')
        shouldFail(JsonException) { parser.parseText('[1') }
        parser.parseText('[1,')
        shouldFail(JsonException) { parser.parseText('[1, 2') }
        parser.parseText('[1, 2, [3, 4]')
    }


    void testBackSlashEscaping() {
        def json = new JsonBuilder()

        json.person {
            name "Guill\\aume"
            age 33
            pets "Hector", "Felix"
        }

        def jsonstring = json.toString()

        def slurper = new JsonSlurper()
        assert slurper.parseText(jsonstring).person.name == "Guill\\aume"

        assert parser.parseText('{"a":"\\\\"}') == [a: '\\']
        assert parser.parseText('{"a":"C:\\\\\\"Documents and Settings\\"\\\\"}') == [a: 'C:\\"Documents and Settings"\\']
        assert parser.parseText('{"a":"c:\\\\GROOVY5144\\\\","y":"z"}') == [a: 'c:\\GROOVY5144\\', y: 'z']

        assert parser.parseText('["c:\\\\GROOVY5144\\\\","d"]') == ['c:\\GROOVY5144\\', 'd']

            parser.parseText('{"a":"c:\\\"}')
    }


}
