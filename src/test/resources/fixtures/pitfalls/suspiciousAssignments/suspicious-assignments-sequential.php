<?php

function immediateOverrides()
{
    /* case 1: possible override, if itself */
    if ($x) {
        $y = '';
    }
    <error descr="$y is immediately overridden, perhaps it was intended to use 'else' here.">$y = ''</error>;
    /* case 1: possible override, alternative branch */
    if ($x) {
        ;
    } else {
        $y = '';
    }
    <error descr="$y is immediately overridden, perhaps it was intended to use 'else' here.">$y = ''</error>;

    /* false-positive: if ends with an exit point */
    if ($x) {
        $y = '';
        return $y;
    }
    $y = '';
    /* false-positive: 2nd write is optional */
    $t = '';
    if ($x) {
        $t = '';
    }


    /* case 2: guaranteed override */
    $z = '';
    <error descr="$z is immediately overridden, please check this code fragment.">$z</error> = $y;

    /* false-positive: depends on itself */
    $a = '';
    $a = trim($a);
    /* false-positive: accumulation */
    $v[] = '';
    $v[] = '';


    return [$y, $z, $t, $a];
}