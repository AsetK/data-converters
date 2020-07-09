<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

    <xsl:param name="key1"/>
    <xsl:param name="key2"/>
    <xsl:param name="key3"/>

    <xsl:template name="attachment">
        <PACKAGE>
            <DOCUMENT>
                <MODEL>
                    <xsl:value-of select="$key1"/>
                </MODEL>
            </DOCUMENT>
        </PACKAGE>
    </xsl:template>

    <xsl:template match="/">
    <TEST>
                        <PERSON><xsl:value-of select="Person/name"/></PERSON>
                        <PERSON><xsl:value-of select="Person"/></PERSON>
                        <KEY1><xsl:value-of select="$key1"/></KEY1>
                        <KEY2><xsl:value-of select="$key2"/></KEY2>
                        <KEY3><xsl:value-of select="$key3"/></KEY3>
                        <xsl:call-template name="attachment"/>
    </TEST>
    </xsl:template>

</xsl:stylesheet>