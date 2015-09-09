/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.runtime.persistence.adaptermanager;

import com.google.common.collect.Lists;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;

import org.apache.isis.applib.annotation.Aggregated;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.profiles.Localization;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.app.IsisMetaModel;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;
import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.runtime.persistence.adapter.PojoAdapterFactory;
import org.apache.isis.core.runtime.system.persistence.OidGenerator;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.apache.isis.progmodels.dflt.ProgrammingModelFacetsJava5;

public class AdapterManagerDefault_aggregateAdapters {

    public static class Customer {
        // {{ Name (property)
        private Name name;

        @MemberOrder(sequence = "1")
        public Name getName() {
            return name;
        }

        public void setName(final Name name) {
            this.name = name;
        }
        // }}
    }
    
    @Aggregated
    public static class Name {}
    
    public static class CustomerRepository {
        public Customer x() { return null; }
    }


    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private OneToManyAssociation mockCollection;

    @Mock
    private RuntimeContext mockRuntimeContext;
    
    @Mock
    private OidGenerator mockOidGenerator;

    @Mock
    protected Localization mockLocalization;
    
    @Mock
    private AuthenticationSession mockAuthenticationSession;
    @Mock
    private SpecificationLoaderSpi mockSpecificationLoader;

    @Mock
    private IsisConfiguration mockConfiguration;
    
    private IsisMetaModel isisMetaModel;
    
    private PojoAdapterFactory adapterFactory;
    
    private AdapterManagerDefault adapterManager;
    
    private Customer rootObject;
    private Name aggregatedObject;
    
    private ObjectAdapter persistentParentAdapter;
    private ObjectAdapter aggregatedAdapter;


    
    
    @Before
    public void setUp() throws Exception {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);

        context.ignoring(mockRuntimeContext);
        context.ignoring(mockAuthenticationSession);
        context.ignoring(mockConfiguration);
        
        isisMetaModel = new IsisMetaModel(
                                mockRuntimeContext,
                                new ProgrammingModelFacetsJava5(),
                                Lists.newArrayList(new CustomerRepository()));
        isisMetaModel.init();

        adapterFactory = new PojoAdapterFactory(adapterManager, mockSpecificationLoader, mockAuthenticationSession) {
            @Override
            protected Localization getLocalization() {
                return mockLocalization;
            }
        };

        adapterManager = new AdapterManagerDefault() {
            @Override
            protected SpecificationLoaderSpi getSpecificationLoader() {
                return isisMetaModel.getSpecificationLoader();
            }
            @Override
            protected PojoAdapterFactory getObjectAdapterFactory() {
                return adapterFactory;
            }
            @Override
            public OidGenerator getOidGenerator() {
                return mockOidGenerator;
            }
            @Override
            protected ServicesInjector getServicesInjector() {
                return isisMetaModel.getDependencyInjector();
            }
        };

        rootObject = new Customer();
        aggregatedObject = new Name();
        
        persistentParentAdapter = adapterManager.mapRecreatedPojo(
                RootOid.create(ObjectSpecId.of("CUS"), "1"), rootObject);
    }


}
