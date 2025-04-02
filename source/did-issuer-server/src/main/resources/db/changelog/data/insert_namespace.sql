INSERT INTO namespace (
    id, namespace_id, name, ref, schema_claims
) VALUES (
             1,
             'org.iso.18013.5',
             'ISO/IEC 18013-5:2021 - Personal identification',
             'https://www.iso.org/standard/69084.html',
             '{"namespace":{"id":"org.iso.18013.5","name":"ISO/IEC 18013-5:2021 - Personal identification","ref":"https://www.iso.org/standard/69084.html"},"items":[{"id":"family_name","caption":"Family Name","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"given_name","caption":"Given Name","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"birth_date","caption":"Birth date","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"address","caption":"Address","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"document_number","caption":"Document Number","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"issue_date","caption":"Issue Date","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null}]}'
         ),
      (
           2,
           'org.opendid.v1.national_id',
           'OpenDID National ID',
           'https://opendid.org/schema/v1/claim',
           '{"namespace":{"id":"org.opendid.v1.national_id","name":"OpenDID National ID","ref":"https://opendid.org/schema/v1/claim"},"items":[{"id":"user_name","caption":"Name","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"birth_date","caption":"Birth date","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"issue_date","caption":"Issue Date","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null},{"id":"address","caption":"Address","type":"text","format":"plain","hideValue":false,"location":null,"required":null,"description":null,"i18n":null}]}'
      );