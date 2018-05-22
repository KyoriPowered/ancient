/*
 * This file is part of ancient, licensed under the MIT License.
 *
 * Copyright (c) 2017-2018 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.ancient.model;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * An abstract implementation of a model collection.
 *
 * @param <P> the partial model type
 */
public abstract class AbstractModelCollection<P extends PartialModel> implements ModelCollection<P> {
  private static final JsonWriterSettings JSON_WRITER_SETTINGS = JsonWriterSettings.builder()
    .objectIdConverter((value, writer) -> writer.writeString(value.toHexString()))
    .build();

  protected abstract @NonNull MongoCollection<Document> collection();

  protected final @NonNull FindIterable<Document> find() {
    return this.collection().find();
  }

  protected abstract @NonNull Gson gson();

  /**
   * Deserialize a document into a partial model.
   *
   * @param document the document
   * @param type the partial model class
   * @param <M> the partial model type
   * @return the partial model
   */
  protected final <M extends P> M deserialize(final @NonNull Document document, final Class<M> type) {
    final String json = document.toJson(JSON_WRITER_SETTINGS);
    return this.gson().fromJson(json, type);
  }

  /**
   * Deserialize a document into a partial model.
   *
   * @param document the document
   * @param type the partial model class
   * @param <M> the partial model type
   * @return the partial model
   */
  protected final <M extends P> Optional<M> maybeDeserialize(final @Nullable Document document, final Class<M> type) {
    if(document == null) {
      return Optional.empty();
    }
    return Optional.of(this.deserialize(document, type));
  }

  /**
   * Serialize a partial model into a document.
   *
   * @param model the partial model
   * @return the document
   */
  protected final Document serialize(final P model) {
    return Document.parse(this.gson().toJson(model));
  }
}
