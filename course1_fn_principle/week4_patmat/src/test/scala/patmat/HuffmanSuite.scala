package patmat

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
	trait TestTrees {
		val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
		val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
	}


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a','b','d'))
    }
  }

  test("times") {
    val chars = List('a', 'b', 'a')
    val counts = List(('a', 2), ('b', 1))
    assert(times(chars) === counts)
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }


  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e',1), Leaf('t',2), Leaf('x',3)))
  }


  test("combine of some leaf list") {

    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    //val actual = List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4))
    val actual = List(Fork(Fork(Leaf('e',1), Leaf('t',2),List('e', 't'),3),
                           Leaf('x',4),
                           List('e', 't', 'x'),
                           7)
                      )

    assert(combine(leaflist) === actual)
  }

  // http://huffman.ooz.ie/
  test("createCodeTree") {
    val chars = string2Chars("abac")
    val actual = Fork(Leaf('a', 2),
                      Fork(Leaf('c', 1),
                           Leaf('b', 1),
                           List('c', 'b'), 2),
                      List('a', 'c', 'b'),
                      4)
    assert(createCodeTree(chars) === actual)
  }

  test("codeBits") {
    val table: CodeTable = List(('a', List(0, 1)), ('b', List(1,0)))
    assert(codeBits(table)('a') === List(0,1))
    assert(codeBits(table)('z') === List[Bit]())
  }

  test("convert") {
    val chars = string2Chars("abac")
    val tree = createCodeTree(chars)
    val actual = List(('a',List(0)), ('c',List(1, 0)), ('b',List(1, 1)))
    assert(convert(tree) === actual)
  }

  test("encode") {
    val chars = string2Chars("abac")
    val tree = createCodeTree(chars)
    val encoded = encode(tree)(chars)
    val decoded = decode(tree, encoded)
    assert(decoded === chars)
  }

  test("decode and encode a very short text should be identity") {
    new TestTrees {
      assert(decode(t1, encode(t1)("ab".toList)) === "ab".toList)
    }
  }

  test("french"){
    assert(decode(frenchCode, secret).mkString === "huffmanestcool")
  }
}
