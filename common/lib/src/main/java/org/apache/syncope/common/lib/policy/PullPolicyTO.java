/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.lib.policy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "pullPolicy")
@XmlType
@ApiModel(parent = PolicyTO.class)
public class PullPolicyTO extends PolicyTO {

    private static final long serialVersionUID = 993024634238024242L;

    private PullPolicySpec specification;

    @XmlTransient
    @JsonProperty("@class")
    @ApiModelProperty(
            name = "@class", required = true, example = "org.apache.syncope.common.lib.policy.PullPolicyTO")
    @Override
    public String getDiscriminator() {
        return getClass().getName();
    }

    @JsonProperty(required = true)
    @XmlElement(required = true)
    public PullPolicySpec getSpecification() {
        return specification;
    }

    public void setSpecification(final PullPolicySpec specification) {
        this.specification = specification;
    }

}
