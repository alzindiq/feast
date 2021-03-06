/*
 * Copyright 2019 The Feast Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feast.ingestion.transform;

import feast.ingestion.values.PFeatureRows;
import feast.types.FeatureRowExtendedProto.FeatureRowExtended;
import feast.types.FeatureRowProto.FeatureRow;
import lombok.AllArgsConstructor;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;

/**
 * This class is a work around to make some refactoring easier. PFeatureRows should be deprecated.
 */
@AllArgsConstructor
public class CoalescePFeatureRows extends
    PTransform<PFeatureRows, PFeatureRows> {

  private long delaySeconds;
  private long timeoutSeconds;

  @Override
  public PFeatureRows expand(PFeatureRows input) {
    PCollection<FeatureRowExtended> output = input.getMain()
        .apply(MapElements.into(TypeDescriptor.of(FeatureRow.class))
            .via(FeatureRowExtended::getRow))
        .apply(new CoalesceFeatureRows(delaySeconds, timeoutSeconds))
        .apply(new ToFeatureRowExtended());
    return PFeatureRows.of(output, input.getErrors());
  }
}
