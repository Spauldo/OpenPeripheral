﻿<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />
  <xsl:template match="/documentation">
  <html>
  <head>
  <title>Open Peripheral API Documentation</title>
  <style type="text/css">
body {
            background-color: white;
            color: black;
}

.major {
            margin-top: 4px;
            margin-left: 4px;
            margin-right: 4px;
            margin-bottom: 20px;
            border-width: 2px;
            border-style: solid;
            border-top-color: silver;
            border-left-color: silver;
            border-right-color: gray;
            border-bottom-color: gray;
            padding: 3px;
            background-color: #faf0e6;
}

.method {
            margin: 2px;
            border-width: 1px;
            border-style: solid;
            border-color: gray;
            padding: 2px;
}

.arguments {
            padding-left: 3em;
}
  </style>
  </head>
  <body>
  <h1>Your OpenPeripherals API Documentation</h1>
  <p>This documentation is specific to your mods.  If it seems empty, try using peripheral.wrap() on a few things and running some code (any code) on the terminal glasses.  This will generate more peripherals for you to use.</p>
  <h2>Contents:</h2>
  <p>Peripherals:</p>
  <ul>
  <xsl:for-each select="peripheral">
    <li><a><xsl:attribute name="href">#periph.<xsl:value-of select="@class" /></xsl:attribute><xsl:value-of select="simpleName/text()" /></a></li>
  </xsl:for-each>
  </ul>
  <p>Lua Objects:</p>
  <ul>
  <xsl:for-each select="luaObject">
    <li><a><xsl:attribute name="href">#lua.<xsl:value-of select="@class" /></xsl:attribute><xsl:value-of select="simpleName/text()" /></a></li>
  </xsl:for-each>
  </ul>
  <p>Adapters:</p>
  <ul>
  <xsl:for-each select="adapter">
    <li><a><xsl:attribute name="href">#adapt.<xsl:value-of select="@class" /></xsl:attribute><xsl:value-of select="source/text()" /></a></li>
  </xsl:for-each>
  </ul>
  
  <xsl:for-each select="peripheral">
    <div class="major">
    <xsl:attribute name="id">periph.<xsl:value-of select="@class" /></xsl:attribute>
    <h1><xsl:value-of select="simpleName/text()" /><xsl:text> - </xsl:text><xsl:value-of select="name/text()" /></h1>
    <p>A peripheral</p>
    <xsl:for-each select="method">
      <div class="method">

      <h2><code><xsl:value-of select="@name" /><xsl:value-of select="signature/text()" /></code></h2>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>
  
  <xsl:for-each select="luaObject">
    <div class="major">
    <xsl:attribute name="id">lua.<xsl:value-of select="@class" /></xsl:attribute>
    <h1><xsl:value-of select="simpleName/text()" /></h1>
    <p>A Lua Object</p>
    <xsl:for-each select="method">
      <div class="method">
      <h2><code><xsl:value-of select="@name" /><xsl:value-of select="signature/text()" /></code></h2>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>
  
  <xsl:for-each select="adapter">
    <div class="major">
    <xsl:attribute name="id">adapt.<xsl:value-of select="@class" /></xsl:attribute>
    <h1><xsl:value-of select="source/text()" /></h1>
    <xsl:if test="target/text()"><p>Target: <code><xsl:value-of select="target/text()" /></code></p></xsl:if>
    <p>An Adapter</p>
    <p>Location: <xsl:value-of select="@location" /></p>
    <p>Type: <xsl:value-of select="@type" /></p>
    <xsl:for-each select="method">
      <div class="method">
      <h2><code><xsl:value-of select="names/name/text()"/><xsl:value-of select="signature/text()" /></code></h2>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>

  </body>
  </html>
  </xsl:template>
</xsl:stylesheet>
