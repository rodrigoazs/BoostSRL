# BoostSRL Version 1.0

Developed by [Jude Shavlik](http://pages.cs.wisc.edu/~shavlik/), [Tushar Khot](http://pages.cs.wisc.edu/~tushar/), [Sriraam Natarajan](https://utdallas.edu/~sriraam.natarajan/), and [members of the STARAI Lab](http://www.indiana.edu/~iustarai/people.html).

---

This algorithm was modified to perform Transfer Learning/Theory Revision in Boosted RDNs.


### Basic Usage

Transfer learning is done by creating a file named transfer.txt in the same root as background.txt

  ```text
  source: actor(person).
  source: movie(movie,person).
  source: female(person).
  source: workedunder(person,person).
  source: genre(person,genre).
  source: director(person).
  target: sameproject(project,project).
  target: projectmember(project,person).
  target: yearsinprogram(person,year).
  target: courselevel(course,level).
  target: publication(title,person).
  target: professor(person).
  target: tempadvisedby(person,person).
  target: student(person).
  target: hasposition(person,faculty).
  target: sameperson(person,person).
  target: samecourse(course,course).
  target: advisedby(person,person).
  target: inphase(person,prequals).
  target: ta(course,person,quarter).
  setMap: workedunder(A,B)=advisedby(A,B).
  setMap: recursion_workedunder(A,B)=recursion_advisedby(A,B).
  setParam: searchArgPermutation=true.
  setParam: searchEmpty=false.
  setParam: allowSameTargetMap=false.
  ```
  
Source should contain the predicates to be transferred from source domain among with their respective types. Target are for predicates from the target domain. The mapping procedure will find a mapping respecting the type constraints. The setMap will define already known mappings for the predicates.

Three search bias to conduct the way the algorithm performs the mapping are included. The first one, called ssearchArgPermutation, allows searching for the permutation of all arguments in the target predicate to check if one of them makes the source and target predicates compatible. It allows for example, the mapping of a source predicate with the inverse relation of a target predicate (e.g.wokedunder(A,B) -> advises(B,A), which is the same as advisedby(A,B)). The second search bias, named searchEmpty, allows generating an additional "empty" mapping even if there is a compatible target predicate to map the source predicate. The  last one, named allowSameTargetMap, allows mapping distinct source predicates to the same target predicate. If this bias is not used, the algorithm finds a one-to-one correspondence between source and target predicates (except for ”empty mapping).

### Source code for **BoostSRL**

<!--

Commenting out until we properly set up Travis CI.

| License | Build Status |
| --- | --- |
| [![][license img]][license] | [![Build Status](https://travis-ci.org/boost-starai/BoostSRL.svg?branch=master)](https://travis-ci.org/boost-starai/BoostSRL) |

-->

[![][license img]][license]

[BoostSRL Wiki](https://starling.utdallas.edu/software/boostsrl/wiki/) | [Group Website](https://starling.utdallas.edu) | [Downloads Page](https://starling.utdallas.edu/software/boostsrl/) | [Datasets](https://starling.utdallas.edu/datasets/)

Questions?

Contact Sriraam Natarajan: natarasr(at)indiana(dot)edu

[license]:license.txt
[license img]:https://img.shields.io/aur/license/yaourt.svg
