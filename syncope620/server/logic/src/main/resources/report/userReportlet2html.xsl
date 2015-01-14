<?xml version="1.0" encoding="UTF-8"?>
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:template match="reportlet[@class='org.apache.syncope.core.report.UserReportlet']">
    
    <h3>Reportlet: <xsl:value-of select="@name"/></h3>
    
    <xsl:for-each select="user">
      <h4>User <xsl:value-of select="@username"/></h4>
      
      <table style="border: 1px solid black;">
        <tr>
          <td>Status:</td>
          <td>
            <xsl:value-of select="@status"/>
          </td>
        </tr>
        <xsl:if test="string-length(@creationDate) &gt; 0">
          <tr>
            <td>Creation Date:</td>
            <td>
              <xsl:value-of select="@creationDate"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="string-length(@lastLoginDate) &gt; 0">
          <tr>
            <td>Last Login Date:</td>
            <td>
              <xsl:value-of select="@lastLoginDate"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="string-length(@changePwdDate) &gt; 0">
          <tr>
            <td>Change Password Date:</td>
            <td>
              <xsl:value-of select="@changePwdDate"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="string-length(@passwordHistorySize) &gt; 0">
          <tr>
            <td>Password History Size:</td>
            <td>
              <xsl:value-of select="@passwordHistorySize"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="string-length(@failedLoginCount) &gt; 0">
          <tr>
            <td>Number of Failed Login Attempts:</td>
            <td>
              <xsl:value-of select="@failedLoginCount"/>
            </td>
          </tr>
        </xsl:if>
      </table>

      <xsl:call-template name="attributes">
        <xsl:with-param name="label">Attributes</xsl:with-param>
        <xsl:with-param name="node" select="attributes/attribute"/>
      </xsl:call-template>

      <!--<xsl:if test="string-length(derivedAttributes/derivedAttribute) &gt; 0">-->
      <xsl:choose>
        <xsl:when test="string-length(derivedAttributes/derivedAttribute) &gt; 0">
          <xsl:call-template name="attributes">
            <xsl:with-param name="label">Derived Attributes</xsl:with-param>
            <xsl:with-param name="node" select="derivedAttributes/derivedAttribute"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <h5>THIS USER HASN'T DERIVED ATTRIBUTES</h5>
        </xsl:otherwise>
      </xsl:choose>
      <!--</xsl:if>-->
      <xsl:choose>
        <xsl:when test="string-length(virtualAttributes/virtualAttribute) &gt; 0">
          <xsl:call-template name="attributes">
            <xsl:with-param name="label">Virtual Attributes</xsl:with-param>
            <xsl:with-param name="node" select="virtualAttributes/virtualAttribute"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <h5>THIS USER HASN'T VIRTUAL ATTRIBUTES</h5>
        </xsl:otherwise>
      </xsl:choose>
      
      <xsl:choose>
        <xsl:when test="string-length(memberships/membership) &gt; 0">
          <h4>Memberships</h4>
          <xsl:for-each select="memberships/membership">
            <h5>Role: <xsl:value-of select="@roleName"/>(<xsl:value-of select="@roleId"/>)</h5>
            <blockquote>
              <xsl:choose>
                <xsl:when test="string-length(attributes/attribute) &gt; 0">
                  <xsl:call-template name="attributes">
                    <xsl:with-param name="label">Attributes</xsl:with-param>
                    <xsl:with-param name="node" select="attributes/attribute"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <h5>THIS ROLE HASN'T ATTRIBUTES</h5>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:choose>
                <xsl:when test="string-length(derivedAttributes/derivedAttribute) &gt; 0">
                  <xsl:call-template name="attributes">
                    <xsl:with-param name="label">Derived Attributes</xsl:with-param>
                    <xsl:with-param name="node" select="derivedAttributes/derivedAttribute"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <h5>THIS ROLE HASN'T DERIVED ATTRIBUTES</h5>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:choose>
                <xsl:when test="string-length(virtualAttributes/virtualAttribute) &gt; 0">
                  <xsl:call-template name="attributes">
                    <xsl:with-param name="label">Virtual Attributes</xsl:with-param>
                    <xsl:with-param name="node" select="virtualAttributes/virtualAttribute"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <h5>THIS ROLE HASN'T VIRTUAL ATTRIBUTES</h5>
                </xsl:otherwise>
              </xsl:choose>
              <!-- 'roleResources" is defined in roleReportlet2html.xsl -->
              <xsl:call-template name="roleResources">
                <xsl:with-param name="node" select="resources/resource"/>
              </xsl:call-template>
            </blockquote>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <h5>THIS USER ISN'T ASSIGNED TO A ROLE</h5>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="string-length(resources) &gt; 0">
        <xsl:call-template name="resources">
          <xsl:with-param name="node" select="resources/resource"/>
        </xsl:call-template>
      </xsl:if>
      <hr/>
    </xsl:for-each>
       
  </xsl:template>

  <xsl:template name="attributes">
    <xsl:param name="label"/>
    <xsl:param name="node"/>

    <h4>
      <xsl:value-of select="$label"/>
    </h4>
    
    <table>
      <thead>
        <tr>
          <th>Schema name</th>
          <th>Value(s)</th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="$node">
          <xsl:if test="string-length(value/text()) &gt; 0">
            <tr>
              <td>
                <xsl:value-of select="@name"/>
              </td>
              <td>
                <ul>
                  <xsl:for-each select="value">
                    <li>
                      <xsl:value-of select="text()"/>
                    </li>
                  </xsl:for-each>
                </ul>
              </td>
            </tr>
          </xsl:if>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template name="resources">
    <xsl:param name="node"/>
    
    <h4>Resources</h4>
    <ul>
      <xsl:for-each select="$node">
        <li>
          <xsl:value-of select="@name"/>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>
</xsl:stylesheet>