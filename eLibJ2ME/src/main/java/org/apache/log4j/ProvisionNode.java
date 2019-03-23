/*
 * Copyright (C) The Apache Software Foundation. All rights reserved. This software is published
 * under the terms of the Apache Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */

package org.apache.log4j;

import java.util.Vector;

class ProvisionNode extends Vector {

  /**
   *
   */
  private static final long serialVersionUID = 8172287566760297589L;

  ProvisionNode(final Category cat) {
    super();
    addElement(cat);
  }
}
